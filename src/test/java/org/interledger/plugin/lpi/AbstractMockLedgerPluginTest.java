package org.interledger.plugin.lpi;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.testng.AssertJUnit.fail;

import org.interledger.InterledgerAddress;
import org.interledger.cryptoconditions.Fulfillment;
import org.interledger.cryptoconditions.PreimageSha256Fulfillment;
import org.interledger.ilp.InterledgerPayment;
import org.interledger.ilp.InterledgerProtocolError;
import org.interledger.ilp.InterledgerProtocolError.ErrorCode;
import org.interledger.plugin.lpi.MockLedgerPlugin.ExtendedLedgerPluginConfig;
import org.interledger.plugin.lpi.events.IncomingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.IncomingTransferRejectedEvent;
import org.interledger.plugin.lpi.events.OutgoingTransferPreparedEvent;
import org.interledger.plugin.lpi.exceptions.InvalidFulfillmentException;
import org.interledger.plugin.lpi.exceptions.InvalidTransferException;
import org.interledger.plugin.lpi.exceptions.LedgerPluginNotConnectedException;
import org.interledger.plugin.lpi.exceptions.TransferNotFoundException;
import org.interledger.plugin.lpi.handlers.LedgerPluginEventHandler;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.money.CurrencyUnit;
import javax.money.Monetary;

/**
 * An abstract class that provides a common test-harness for the Mock ledger plugins defined in this
 * package.
 */
public abstract class AbstractMockLedgerPluginTest {

  protected static final byte[] PREIMAGE = "quinquagintaquadringentilliardth".getBytes();
  protected static final byte[] ALTERNATE_PREIMAGE = "11inquagintaquadringentilliard11".getBytes();

  protected static final InterledgerAddress LEDGER_PREFIX = InterledgerAddress.of("test1.foo.");
  protected static final InterledgerAddress CONNECTOR_ACCOUNT_ON_LEDGER = LEDGER_PREFIX
      .with("connector");

  @Mock
  protected LedgerPluginEventHandler ledgerPluginEventHandlerMock;

  protected LedgerInfo ledgerInfo;

  protected MockLedgerPlugin mockLedgerPlugin;

  protected Map<String, String> getOptions() {
    return ImmutableMap.of(
        MockLedgerPlugin.LEDGER_PREFIX, LEDGER_PREFIX.getValue(),
        MockLedgerPlugin.CONNECTOR_ACCOUNT, CONNECTOR_ACCOUNT_ON_LEDGER.getValue(),
        MockLedgerPlugin.EXPECTED_CURRENCY_UNIT, "USD",
        "password", "password"
    );
  }

  @Test
  public void testGetLedgerMetadata() throws Exception {
    assertThat(this.mockLedgerPlugin.getLedgerInfo(), is(ledgerInfo));
  }

  @Test(expectedExceptions = LedgerPluginNotConnectedException.class)
  public void testGetConnectorAccount_Disconnected() throws Exception {
    try {
      this.mockLedgerPlugin.disconnect();
      assertThat(mockLedgerPlugin.isConnected(), is(false));
      this.mockLedgerPlugin.getConnectorAccount();
      fail();
    } catch (LedgerPluginNotConnectedException e) {
      assertThat(e.getPluginLedgerPrefix(), is(LEDGER_PREFIX));
      throw e;
    }
  }

  @Test
  public void testGetConnectorAccount_Connected() throws Exception {
    this.mockLedgerPlugin.connect();
    assertThat(this.mockLedgerPlugin.getConnectorAccount(), is(LEDGER_PREFIX.with("connector")));
  }

  @Test
  public void testConnect() throws Exception {
    // The test connects the plugin to the ledger by default, so disconnect first.
    this.mockLedgerPlugin.disconnect();
    Mockito.reset(ledgerPluginEventHandlerMock);

    this.mockLedgerPlugin.connect();
    assertThat(mockLedgerPlugin.isConnected(), is(true));

    verify(ledgerPluginEventHandlerMock).onConnect(any());
    verifyNoMoreInteractions(ledgerPluginEventHandlerMock);
    assertThat(mockLedgerPlugin.getSimulatedLedger().getConnections().size(), is(1));
  }

  @Test
  public void testDisconnect() throws Exception {
    // The test connects the plugin to the ledger by default, so no need to call it again here...
    this.mockLedgerPlugin.disconnect();
    assertThat(mockLedgerPlugin.isConnected(), is(false));

    verify(ledgerPluginEventHandlerMock).onDisconnect(any());
    verifyNoMoreInteractions(ledgerPluginEventHandlerMock);
    assertThat(mockLedgerPlugin.getSimulatedLedger().getConnections().size(), is(0));
  }

  @Test
  public void testSendTransfer() {
    final Transfer transfer = constructOutgoingTransferFromConnector(UUID.randomUUID());
    this.mockLedgerPlugin.sendTransfer(transfer);

    verify(ledgerPluginEventHandlerMock)
        .onTransferPrepared(Mockito.<OutgoingTransferPreparedEvent>any());
    verifyNoMoreInteractions(ledgerPluginEventHandlerMock);
  }

  @Test(expectedExceptions = InvalidTransferException.class)
  public void testSendTransferToSelf() {
    final Transfer transfer = this.constructTransfer(
        UUID.randomUUID(), CONNECTOR_ACCOUNT_ON_LEDGER, CONNECTOR_ACCOUNT_ON_LEDGER
    );

    try {
      this.mockLedgerPlugin.sendTransfer(transfer);
      fail("Shouldn't be able to send a transfer to yourself!");
    } catch (InvalidTransferException e) {
      assertThat(e.getPluginLedgerPrefix(), is(LEDGER_PREFIX));
      assertThat(e.getTransferId(), is(transfer.getTransferId()));
      verifyZeroInteractions(ledgerPluginEventHandlerMock);
      throw e;
    }
  }

  /**
   * Simulate an already-prepared incoming transfer that this test will fulfill...
   */
  @Test
  public void testFulfillCondition() {
    // Directly add the transfer to the Simulated Ledger as if it was added long ago...
    final Transfer transfer = constructIncomingTransferToConnector(UUID.randomUUID());
    mockLedgerPlugin.getSimulatedLedger().getTransfers()
        .putIfAbsent(transfer.getTransferId(),
            ImmutableTransferHolder.builder().transfer(transfer).build());

    final Fulfillment fulfillment = new PreimageSha256Fulfillment(PREIMAGE);
    this.mockLedgerPlugin.fulfillCondition(transfer.getTransferId(), fulfillment);

    verify(ledgerPluginEventHandlerMock)
        .onTransferFulfilled(Mockito.<IncomingTransferFulfilledEvent>any());
    verifyNoMoreInteractions(ledgerPluginEventHandlerMock);
  }

  /**
   * Simulate an already-prepared incoming transfer that this test will reject...
   */
  @Test(expectedExceptions = InvalidFulfillmentException.class)
  public void testFulfillWithInvalidCondition() {
    // Directly add the transfer to the Simulated Ledger as if it was added long ago...
    final Transfer transfer = constructIncomingTransferToConnector(UUID.randomUUID());
    mockLedgerPlugin.getSimulatedLedger().getTransfers()
        .putIfAbsent(transfer.getTransferId(),
            ImmutableTransferHolder.builder().transfer(transfer).build());

    final Fulfillment fulfillment = new PreimageSha256Fulfillment(ALTERNATE_PREIMAGE);

    try {
      this.mockLedgerPlugin.fulfillCondition(transfer.getTransferId(), fulfillment);
      fail();
    } catch (InvalidFulfillmentException e) {
      assertThat(e.getPluginLedgerPrefix(), is(LEDGER_PREFIX));
      assertThat(e.getMessage(), is(nullValue()));
      verifyZeroInteractions(ledgerPluginEventHandlerMock);
      throw e;
    }
  }

  /**
   * Assert that an exception is thrown if a fulfill attempt is make on an unknown transfer.
   */
  @Test(expectedExceptions = TransferNotFoundException.class)
  public void testFulfillMissingTranscation() {
    final Fulfillment fulfillment = new PreimageSha256Fulfillment(PREIMAGE);

    try {
      this.mockLedgerPlugin.fulfillCondition(TransferId.of(UUID.randomUUID()), fulfillment);
      fail("Expected an exception!");
    } catch (TransferNotFoundException e) {
      verifyZeroInteractions(ledgerPluginEventHandlerMock);
      assertThat(e.getPluginLedgerPrefix(), is(LEDGER_PREFIX));
      assertThat(e.getMessage(), is(nullValue()));
      throw e;
    }
  }

  /**
   * This test creates a transfer (with a source and destination unrelated to the connector account)
   * on the underlying ledger and expects the ledger plugin to _not_ receive any ledger plugin
   * events from the underlying ledger.
   */
  @Test
  public void testTranferUnrelatedToConnector() {
    // Directly add the transfer to the Simulated Ledger as if it was added long ago...
    final Transfer transfer = constructTransfer(
        UUID.randomUUID(), LEDGER_PREFIX.with("source"), LEDGER_PREFIX.with("destination")
    );
    mockLedgerPlugin.getSimulatedLedger().getTransfers()
        .putIfAbsent(transfer.getTransferId(),
            ImmutableTransferHolder.builder().transfer(transfer).build());

    final Fulfillment fulfillment = new PreimageSha256Fulfillment(PREIMAGE);
    this.mockLedgerPlugin.fulfillCondition(transfer.getTransferId(), fulfillment);

    verifyZeroInteractions(ledgerPluginEventHandlerMock);
  }

  @Test
  public void testRejectTransfer() {
    // Simulate an already-prepared incoming transfer that this test will reject...
    final Transfer transfer = constructIncomingTransferToConnector(UUID.randomUUID());
    mockLedgerPlugin.getSimulatedLedger().getTransfers().putIfAbsent(
        transfer.getTransferId(), ImmutableTransferHolder.builder().transfer(transfer).build()
    );

    this.mockLedgerPlugin.rejectIncomingTransfer(transfer.getTransferId(), constructIlpError());

    verify(ledgerPluginEventHandlerMock)
        .onTransferRejected(Mockito.<IncomingTransferRejectedEvent>any());
    verifyNoMoreInteractions(ledgerPluginEventHandlerMock);
  }

  protected Transfer constructTransfer(
      final UUID transferId, final InterledgerAddress sourceAccount,
      final InterledgerAddress destinationAccount
  ) {
    return constructTransfer(transferId, sourceAccount, destinationAccount, BigInteger.TEN);
  }

  protected Transfer constructTransfer(
      final UUID transferId, final InterledgerAddress sourceAccount,
      final InterledgerAddress destinationAccount, BigInteger amount
  ) {
    Objects.requireNonNull(transferId);
    Objects.requireNonNull(sourceAccount);
    Objects.requireNonNull(destinationAccount);

    Preconditions.checkArgument(sourceAccount.startsWith(LEDGER_PREFIX));
    Preconditions.checkArgument(destinationAccount.startsWith(LEDGER_PREFIX));

    return ImmutableTransfer.builder()
        .transferId(TransferId.of(transferId))
        .ledgerPrefix(LEDGER_PREFIX)
        .sourceAccount(sourceAccount)
        .amount(amount)
        .destinationAccount(destinationAccount)
        .executionCondition(new PreimageSha256Fulfillment(PREIMAGE).getCondition())
        .expiresAt(Instant.now().plus(10, ChronoUnit.HOURS))
        .interlederPaymentPacket(constructIlpPacket())
        .build();
  }

  protected Transfer constructIncomingTransferToConnector(UUID transferId) {
    return constructTransfer(transferId, LEDGER_PREFIX.with("source"), CONNECTOR_ACCOUNT_ON_LEDGER);
  }

  protected Transfer constructOutgoingTransferFromConnector(UUID transferId) {
    return constructTransfer(transferId, CONNECTOR_ACCOUNT_ON_LEDGER,
        LEDGER_PREFIX.with("destination"));
  }

  protected InterledgerPayment constructIlpPacket() {
    return InterledgerPayment.builder()
        .destinationAccount(LEDGER_PREFIX.with("destination"))
        .destinationAmount(BigInteger.TEN)
        .data(new byte[0])
        .build();
  }

  protected InterledgerProtocolError constructIlpError() {
    return InterledgerProtocolError.builder()
        .errorCode(ErrorCode.T04_INSUFFICIENT_LIQUIDITY)
        .triggeredAt(Instant.now())
        .triggeredByAddress(LEDGER_PREFIX)
        //.data(new byte[0])
        .build();
  }

  protected ExtendedLedgerPluginConfig getLedgerPluginConfig() {
    return new ExtendedLedgerPluginConfig() {

      @Override
      public LedgerPluginTypeId getLedgerPluginTypeId() {
        return LedgerPluginTypeId.of("ilp-plugin-mock");
      }

      @Override
      public InterledgerAddress getLedgerPrefix() {
        return LEDGER_PREFIX;
      }

      @Override
      public InterledgerAddress getConnectorAccount() {
        return CONNECTOR_ACCOUNT_ON_LEDGER;
      }

      @Override
      public CurrencyUnit getExpectedCurrencyUnit() {
        return Monetary.getCurrency("USD");
      }

      @Override
      public Map<String, String> getOptions() {
        return ImmutableMap.of();
      }

      @Override
      public String getPassword() {
        return "password";
      }
    };
  }
}