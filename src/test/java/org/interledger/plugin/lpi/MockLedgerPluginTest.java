package org.interledger.plugin.lpi;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.reset;

import org.interledger.InterledgerAddress;
import org.interledger.plugin.lpi.MockLedgerPlugin.SimulatedLedger;
import org.interledger.plugin.lpi.MockLedgerPlugin.SimulatedLedger.TransferHolder;
import org.interledger.plugin.lpi.MockLedgerPlugin.SimulatedLedger.TransferStatus;

import ch.qos.logback.classic.Level;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

import javax.money.Monetary;

/**
 * A unit test for {@link MockLedgerPlugin} to ensure that it is functioning properly.
 */
public class MockLedgerPluginTest extends AbstractMockLedgerPluginTest {

  @BeforeMethod
  public void setup() {
    MockitoAnnotations.initMocks(this);

    // Enable debug mode...
    ((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME))
        .setLevel(Level.DEBUG);

    ledgerInfo = ImmutableLedgerInfo.builder()
        .currencyPrecision(2)
        .currencyScale(8)
        .currencyUnit(Monetary.getCurrency("USD"))
        .ledgerPrefix(LEDGER_PREFIX)
        .build();
    final SimulatedLedger simulatedLedger = new SimulatedLedger(ledgerInfo);

    // Initialize the ledger plugin under test...
    this.mockLedgerPlugin = new MockLedgerPlugin(getLedgerPluginConfig(), simulatedLedger);
    mockLedgerPlugin.addLedgerPluginEventHandler(ledgerPluginEventHandlerMock);

    mockLedgerPlugin.connect();

    // Reset the event handler so we don't count the "connect" event, in general
    reset(ledgerPluginEventHandlerMock);
  }

  @Test
  public void testGetAccountBalance() {

    final BigInteger ONE_HUNDRED = BigInteger.valueOf(100);

    final InterledgerAddress sourceAccount = LEDGER_PREFIX.with("source");
    final InterledgerAddress destinationAccount = LEDGER_PREFIX.with("destination");

    final SimulatedLedger simulatedLedger = mockLedgerPlugin.getSimulatedLedger();

    // Initial Funding Funding
    this.doTransfer(LEDGER_PREFIX.with("funding"), sourceAccount, ONE_HUNDRED,
        TransferStatus.EXECUTED);
    assertThat(simulatedLedger.getAccountBalance(sourceAccount), is(ONE_HUNDRED));
    this.doTransfer(LEDGER_PREFIX.with("funding"), destinationAccount, ONE_HUNDRED,
        TransferStatus.EXECUTED);
    assertThat(simulatedLedger.getAccountBalance(destinationAccount), is(ONE_HUNDRED));

    ////////////
    // Transfers from Source Account to Destination....
    ////////////
    this.doTransfer(sourceAccount, destinationAccount, BigInteger.ONE, TransferStatus.PREPARED);
    assertThat(simulatedLedger.getAccountBalance(sourceAccount), is(BigInteger.valueOf(99)));
    assertThat(simulatedLedger.getAccountBalance(destinationAccount), is(ONE_HUNDRED));

    this.doTransfer(sourceAccount, destinationAccount, BigInteger.ONE, TransferStatus.EXECUTED);
    assertThat(simulatedLedger.getAccountBalance(sourceAccount), is(BigInteger.valueOf(98)));
    assertThat(simulatedLedger.getAccountBalance(destinationAccount), is(BigInteger.valueOf(101)));

    this.doTransfer(sourceAccount, destinationAccount, BigInteger.ONE, TransferStatus.REJECTED);
    assertThat(simulatedLedger.getAccountBalance(sourceAccount), is(BigInteger.valueOf(98)));
    assertThat(simulatedLedger.getAccountBalance(destinationAccount), is(BigInteger.valueOf(101)));

    ////////////
    // Transfers from Source Account to Destination....
    ////////////
    this.doTransfer(destinationAccount, sourceAccount, BigInteger.ONE, TransferStatus.PREPARED);
    assertThat(simulatedLedger.getAccountBalance(sourceAccount), is(BigInteger.valueOf(98)));
    assertThat(simulatedLedger.getAccountBalance(destinationAccount), is(ONE_HUNDRED));

    this.doTransfer(destinationAccount, sourceAccount, BigInteger.ONE, TransferStatus.EXECUTED);
    assertThat(simulatedLedger.getAccountBalance(sourceAccount), is(BigInteger.valueOf(99)));
    assertThat(simulatedLedger.getAccountBalance(destinationAccount), is(BigInteger.valueOf(99)));

    this.doTransfer(destinationAccount, sourceAccount, BigInteger.ONE, TransferStatus.REJECTED);
    assertThat(simulatedLedger.getAccountBalance(sourceAccount), is(BigInteger.valueOf(99)));
    assertThat(simulatedLedger.getAccountBalance(destinationAccount), is(BigInteger.valueOf(99)));
  }

  /**
   * Helper method to initiate a specific type of transfer on the simulated ledger of the mock
   * ledger plugin in this test.
   */
  private void doTransfer(
      final InterledgerAddress sourceAccount,
      final InterledgerAddress destinationAccount,
      final BigInteger amount,
      final TransferStatus transferStatus
  ) {
    Objects.requireNonNull(sourceAccount);
    Objects.requireNonNull(destinationAccount);
    Objects.requireNonNull(amount);
    Objects.requireNonNull(transferStatus);

    final SimulatedLedger simulatedLedger = mockLedgerPlugin.getSimulatedLedger();
    final Transfer transfer = constructTransfer(UUID.randomUUID(), sourceAccount,
        destinationAccount, amount);
    final TransferHolder preparedTransferHolder = ImmutableTransferHolder.builder()
        .transfer(transfer)
        .transferStatus(transferStatus)
        .build();
    simulatedLedger.getTransfers().put(transfer.getTransferId(), preparedTransferHolder);
  }

}