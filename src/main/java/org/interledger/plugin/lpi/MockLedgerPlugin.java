package org.interledger.plugin.lpi;

import org.interledger.InterledgerAddress;
import org.interledger.cryptoconditions.Fulfillment;
import org.interledger.ilp.InterledgerProtocolError;
import org.interledger.ilp.InterledgerProtocolError.ErrorCode;
import org.interledger.plugin.lpi.MockLedgerPlugin.ExtendedLedgerPluginConfig;
import org.interledger.plugin.lpi.events.ImmutableIncomingMessgeRequestEvent;
import org.interledger.plugin.lpi.events.ImmutableIncomingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.ImmutableIncomingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.ImmutableIncomingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.ImmutableIncomingTransferRejectedEvent;
import org.interledger.plugin.lpi.events.ImmutableOutgoingMessgeRequestEvent;
import org.interledger.plugin.lpi.events.ImmutableOutgoingTransferCancelledEvent;
import org.interledger.plugin.lpi.events.ImmutableOutgoingTransferFulfilledEvent;
import org.interledger.plugin.lpi.events.ImmutableOutgoingTransferPreparedEvent;
import org.interledger.plugin.lpi.events.ImmutableOutgoingTransferRejectedEvent;
import org.interledger.plugin.lpi.exceptions.InvalidFulfillmentException;
import org.interledger.plugin.lpi.exceptions.InvalidTransferException;
import org.interledger.plugin.lpi.exceptions.TransferAlreadyFulfilledException;
import org.interledger.plugin.lpi.exceptions.TransferAlreadyRolledBackException;
import org.interledger.plugin.lpi.exceptions.TransferNotFoundException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.immutables.value.Value;
import org.immutables.value.Value.Default;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.money.CurrencyUnit;

/**
 * A demonstration implementation of {@link LedgerPlugin} that simulates an underlying ledger while
 * handling events from the underlying ledger in an asynchronous manner.
 *
 * The following is a high-level component and flow diagram for events emitted from the underlying
 * ledger into this ledger plugin:
 *
 * <pre>
 *           ┌─────────────┐
 *           │LedgerPlugin │
 *        ┌─▶│Event Emitter│
 *        │  └─────────────┘
 *        │         │
 *        │     onEvent
 *        │         ▼
 *        │ ┌──────────────┬──────────────┐
 *        │ │ LedgerPlugin │              │
 *        │ │Event Handlers│ LedgerPlugin │
 *        │ │              │              │
 *        │ └──────────────┴──────────────┘
 *        │                        │
 *        │                        ▼
 *     Ledger               ┌─────────────┐
 *     Events               │  Simulated  │
 *        └─────────────────│   Ledger    │
 *                          └─────────────┘
 * </pre>
 */
public class MockLedgerPlugin
    extends AbstractLedgerPlugin<ExtendedLedgerPluginConfig> implements LedgerPlugin {

  public static final String PLUGIN_TYPE = "ilp-plugin-mock";

  static String LEDGER_PREFIX = "ledger_prefix";
  static String CONNECTOR_ACCOUNT = "connector_account";
  static String EXPECTED_CURRENCY_UNIT = "expected_currency_unit";
  static String TIMEOUT = "timeout";

  private final SimulatedLedger simulatedLedger;

  /**
   * Required-args Constructor.
   *
   * @param ledgerPluginConfig A {@link ExtendedLedgerPluginConfig} of configuration strings for
   *                           this plugin.
   * @param simulatedLedger    A {@link SimulatedLedger} that is used by this mock plugin.
   */
  public MockLedgerPlugin(
      final ExtendedLedgerPluginConfig ledgerPluginConfig, final SimulatedLedger simulatedLedger
  ) {
    super(ledgerPluginConfig);
    this.simulatedLedger = Objects.requireNonNull(simulatedLedger);
  }

  /**
   * Required-args Constructor.
   *
   * @param ledgerPluginConfig       A {@link ExtendedLedgerPluginConfig} of configuration strings
   *                                 for this plugin.
   * @param simulatedLedger          A {@link SimulatedLedger} that is used by this mock plugin.
   * @param ledgerPluginEventEmitter A {@link LedgerPluginEventEmitter} to control how events are
   *                                 emitted to/from the plugin.
   */
  public MockLedgerPlugin(
      final ExtendedLedgerPluginConfig ledgerPluginConfig,
      final SimulatedLedger simulatedLedger,
      final LedgerPluginEventEmitter ledgerPluginEventEmitter
  ) {
    super(ledgerPluginConfig, ledgerPluginEventEmitter);
    this.simulatedLedger = Objects.requireNonNull(simulatedLedger);
  }

  /**
   * Overridable method that allows implementations to perform additional initialization of this
   * plugin.
   */
  protected Map<String, String> afterInitializeLedgerPlugin(
      final Map<String, String> configurationOptions
  ) {
    Objects.requireNonNull(configurationOptions);

    // This is to illustrate how one might pass configuration options to a ledger plugin. In this
    // case, the simulated ledger requires a password of "password" when the connection is
    // initiated.

    if (configurationOptions.containsKey("password") == false) {
      return ImmutableMap.<String, String>builder().putAll(configurationOptions)
          .put("password", "password").build();

    } else {
      return configurationOptions;
    }
  }

  @Override
  public void doConnect() {
    // Connect to the underlying ledger
    this.simulatedLedger.connect(
        ImmutableSimulatedLedgerPluginConnection.builder()
            .connectorAddress(this.getLedgerPluginConfig().getConnectorAccount())
            .ledgerPluginEventEmitter(this.getLedgerPluginEventEmitter())
            .password(getLedgerPluginConfig().getPassword())
            .build()
    );
  }

  @Override
  public void doDisconnect() {
    // Disconnect from the underlying ledger
    this.simulatedLedger.disconnect(this.getConnectorAccount());
  }

  // Not implemented in super-class because this information can change....
  @Override
  public LedgerInfo getLedgerInfo() {
    return this.simulatedLedger.getLedgerInfo();
  }

  /**
   * The balance is always 10...
   */
  @Override
  public BigInteger getConnectorBalance() {
    return BigInteger.TEN;
  }

  @Override
  public Optional<Fulfillment> getFulfillment(TransferId transferId) {
    return this.simulatedLedger.getFulfillment(transferId);
  }

  @Override
  public void sendTransfer(final Transfer transfer) {
    Objects.requireNonNull(transfer);
    if (logger.isDebugEnabled()) {
      logger.debug("sendTransfer: {}", transfer);
    }

    // NOTE: For a ledger that might not be able to emit events back to this plugin, the plugin
    // should emit the event on behalf of the ledger.

    this.simulatedLedger.sendTransfer(transfer);
  }

  @Override
  public final void fulfillCondition(final TransferId transferId, final Fulfillment fulfillment) {
    Objects.requireNonNull(transferId);
    Objects.requireNonNull(fulfillment);

    if (logger.isDebugEnabled()) {
      logger.debug("fulfillCondition for transferId: {} and fulfillment: {}", transferId,
          fulfillment);
    }

    this.simulatedLedger.fulfillCondition(transferId, fulfillment);
  }

  @Override
  public void rejectIncomingTransfer(
      final TransferId transferId, final InterledgerProtocolError rejectionReason
  ) {
    Objects.requireNonNull(transferId);
    Objects.requireNonNull(rejectionReason);

    if (logger.isDebugEnabled()) {
      logger
          .debug("rejectIncomingTransfer for transferId: {} and rejectionReason: {}", transferId,
              rejectionReason);
    }
    this.simulatedLedger.rejectIncomingTransfer(transferId, rejectionReason);
  }

  @Override
  public void sendMessage(final Message message) {
    Objects.requireNonNull(message);

    if (logger.isDebugEnabled()) {
      logger.debug("sendMessage for message: {}", message);
    }

    this.simulatedLedger.sendMessage(message);
  }

  public SimulatedLedger getSimulatedLedger() {
    return this.simulatedLedger;
  }

  /**
   * An example of how to configure custom, though typed, configuration for a ledger plugin.
   */
  public interface ExtendedLedgerPluginConfig extends LedgerPluginConfig {

    static ExtendedLedgerPluginConfig from(
        final LedgerPluginConfig ledgerPluginConfig, final String password
    ) {
      return new Impl(ledgerPluginConfig, password);
    }

    /**
     * The password for the connector account on the ledger.
     */
    String getPassword();

    class Impl implements ExtendedLedgerPluginConfig {

      private final LedgerPluginConfig ledgerPluginConfig;
      private final String password;

      public Impl(final LedgerPluginConfig ledgerPluginConfig, final String password) {
        this.password = Objects.requireNonNull(password);
        this.ledgerPluginConfig = Objects.requireNonNull(ledgerPluginConfig);
      }

      /**
       * The password for the connector account on the ledger.
       */
      @Override
      public String getPassword() {
        return this.password;
      }

      /**
       * The type of this ledger plugin.
       */
      @Override
      public LedgerPluginTypeId getLedgerPluginTypeId() {
        return ledgerPluginConfig.getLedgerPluginTypeId();
      }

      /**
       * The identifying ledger prefix for this plugin.
       */
      @Override
      public InterledgerAddress getLedgerPrefix() {
        return ledgerPluginConfig.getLedgerPrefix();
      }

      /**
       * The connector account on the underlying ledger.
       */
      @Override
      public InterledgerAddress getConnectorAccount() {
        return ledgerPluginConfig.getConnectorAccount();
      }

      /**
       * The expected currency-unit for this ledger plugin.
       */
      @Override
      public CurrencyUnit getExpectedCurrencyUnit() {
        return ledgerPluginConfig.getExpectedCurrencyUnit();
      }

      /**
       * The options for a given ledger plugin.
       *
       * @deprecated This method may go away in the future, in favor of a fully-typed configuration
       *     system.
       */
      @Override
      public Map<String, String> getOptions() {
        return ledgerPluginConfig.getOptions();
      }

      @Override
      public String toString() {
        return "ExtendedLedgerPluginConfigImpl{" +
            "ledgerPluginConfig=" + ledgerPluginConfig +
            ", password='" + password + '\'' +
            '}';
      }
    }
  }

  /**
   * A simulated ledger (used only for testing and demonstration purposes) that allows for multiple
   * ledger plugins to connect to it using a unique Interledger address.
   *
   * WARNING: This ledger is not meant for production usage. Among other things, it does not enforce
   * any sort of auth because it is meant for simulation and testing purposes only.
   */
  public static class SimulatedLedger {

    // Ordinarily, this would be provided during the connect operation, but for this Mock plugin,
    // it's passed-in because this whole plugin is simulated.
    private final LedgerInfo ledgerInfo;

    // Holds the transfers for this ledger.
    protected Map<TransferId, TransferHolder> transfers;

    // Supports multiple connections, but only one per Connector address...
    private Map<InterledgerAddress, SimulatedLedgerPluginConnection> connections;

    public SimulatedLedger(final LedgerInfo ledgerInfo) {
      this.ledgerInfo = Objects.requireNonNull(ledgerInfo);
      this.transfers = Maps.newConcurrentMap();
      this.connections = Maps.newConcurrentMap();
    }

    public void connect(final SimulatedLedgerPluginConnection simulatedLedgerPluginConnection) {
      Objects.requireNonNull(simulatedLedgerPluginConnection);

      if (simulatedLedgerPluginConnection.getPassword().equalsIgnoreCase("password")) {
        this.connections.put(
            simulatedLedgerPluginConnection.getConnectorAddress(),
            simulatedLedgerPluginConnection
        );
      } else {
        throw new RuntimeException(
            "Unable to connect to the Simulated Ledger. A password of \"password\" must be set in order to connect!");
      }
    }

    public void disconnect(InterledgerAddress connectionAddress) {
      Objects.requireNonNull(connectionAddress);
      this.connections.remove(connectionAddress);
    }

    public LedgerInfo getLedgerInfo() {
      return ledgerInfo;
    }

    public Map<TransferId, TransferHolder> getTransfers() {
      return this.transfers;
    }

    public Map<InterledgerAddress, SimulatedLedgerPluginConnection> getConnections() {
      return connections;
    }

    public Optional<Fulfillment> getFulfillment(TransferId transferId) {
      return Optional.ofNullable(this.transfers.get(transferId))
          .map(TransferHolder::getExecutionFulfillment)
          .map(Optional::get);
    }

    public void sendTransfer(final Transfer transfer) {

      // Don't allow a transfer with matching to/from.
      if (transfer.getSourceAccount().equals(transfer.getDestinationAccount())) {
        throw new InvalidTransferException(this.getLedgerInfo().getLedgerPrefix(),
            transfer.getTransferId(),
            InterledgerProtocolError.builder()
                .triggeredByAddress(this.getLedgerInfo().getLedgerPrefix())
                .errorCode(ErrorCode.F00_BAD_REQUEST)
                .triggeredAt(Instant.now())
                .build());
      }

      if (Optional.ofNullable(transfers.get(transfer.getTransferId())).isPresent() == true) {
        // This transfer has already been prepared, so ignore it.
        return;
      } else {
        transfers.put(transfer.getTransferId(),
            ImmutableTransferHolder.builder().transfer(transfer).build()
        );

        /////////////////////////
        // Publish an Outgoing Event to any connections that match the sender...
        /////////////////////////
        this.connections.values().stream()
            .filter(
                connection -> connection.getConnectorAddress().equals(transfer.getSourceAccount())
            )
            .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
                ImmutableOutgoingTransferPreparedEvent.builder().transfer(transfer).build()
            ));

        /////////////////////////
        // Publish an Incoming Event to any connections that match the recipient...
        /////////////////////////
        this.connections.values().stream()
            .filter(
                connection -> connection.getConnectorAddress()
                    .equals(transfer.getDestinationAccount())
            )
            .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
                ImmutableIncomingTransferPreparedEvent.builder().transfer(transfer).build()
            ));
      }
    }

    public void fulfillCondition(final TransferId transferId, final Fulfillment fulfillment) {
      Objects.requireNonNull(transferId);
      Objects.requireNonNull(fulfillment);

      // Throw an exception if the transfer is not found...
      final TransferHolder transferHolder = Optional.ofNullable(this.transfers.get(transferId))
          .orElseThrow(() -> new TransferNotFoundException(this.getLedgerInfo().getLedgerPrefix(),
              transferId));

      // Throw an exception if the transfer is already rejected...
      if (transferHolder.getTransferStatus() == TransferStatus.REJECTED) {
        throw new TransferAlreadyRolledBackException(this.getLedgerInfo().getLedgerPrefix(),
            transferId);
      }

      if (fulfillment.verify(transferHolder.getTransfer().getExecutionCondition(), new byte[0])) {
        TransferHolder newTransferHolder = ImmutableTransferHolder.builder().from(transferHolder)
            .executionFulfillment(fulfillment)
            .transferStatus(TransferStatus.EXECUTED).build();
        this.transfers.replace(transferId, transferHolder, newTransferHolder);

        /////////////////////////
        // Publish an Outgoing Event to any connections that match the sender...
        /////////////////////////
        this.connections.values().stream()
            .filter(
                connection -> connection.getConnectorAddress()
                    .equals(newTransferHolder.getTransfer().getSourceAccount())
            )
            .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
                ImmutableOutgoingTransferFulfilledEvent.builder()
                    .transfer(newTransferHolder.getTransfer())
                    .fulfillment(fulfillment)
                    .build()
            ));

        /////////////////////////
        // Publish an Incoming Event to any connections that match the recipient...
        /////////////////////////
        this.connections.values().stream()
            .filter(
                connection -> connection.getConnectorAddress()
                    .equals(newTransferHolder.getTransfer().getDestinationAccount())
            )
            .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
                ImmutableIncomingTransferFulfilledEvent.builder()
                    .transfer(newTransferHolder.getTransfer())
                    .fulfillment(fulfillment)
                    .build()
            ));
      } else {
        throw new InvalidFulfillmentException(this.getLedgerInfo().getLedgerPrefix(), transferId,
            fulfillment);
      }
    }

    public void rejectIncomingTransfer(
        final TransferId transferId, final InterledgerProtocolError rejectionReason
    ) {

      // Throw an exception if the transfer is not found...
      final TransferHolder transferHolder = Optional.ofNullable(this.transfers.get(transferId))
          .orElseThrow(() -> new TransferNotFoundException(this.getLedgerInfo().getLedgerPrefix(),
              transferId));

      // Throw an exception if the transfer is already rejected...
      if (transferHolder.getTransferStatus() == TransferStatus.EXECUTED) {
        throw new TransferAlreadyFulfilledException(this.getLedgerInfo().getLedgerPrefix(),
            transferId);
      }

      TransferHolder newTransferHolder = ImmutableTransferHolder.builder().from(transferHolder)
          .transferStatus(TransferStatus.REJECTED).build();
      this.transfers.replace(transferId, transferHolder, newTransferHolder);

      /////////////////////////
      // Publish an Outgoing Event to any connections that match the sender...
      /////////////////////////
      this.connections.values().stream()
          .filter(
              connection -> connection.getConnectorAddress()
                  .equals(newTransferHolder.getTransfer().getSourceAccount())
          )
          .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
              ImmutableOutgoingTransferRejectedEvent.builder()
                  .transfer(newTransferHolder.getTransfer())
                  .rejectionReason(rejectionReason)
                  .build()
          ));

      /////////////////////////
      // Publish an Incoming Event to any connections that match the recipient...
      /////////////////////////
      this.connections.values().stream()
          .filter(
              connection -> connection.getConnectorAddress()
                  .equals(newTransferHolder.getTransfer().getDestinationAccount())
          )
          .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
              ImmutableIncomingTransferRejectedEvent.builder()
                  .transfer(newTransferHolder.getTransfer())
                  .rejectionReason(rejectionReason)
                  .build()
          ));
    }


    public void sendMessage(final Message message) {
      Objects.requireNonNull(message);

      /////////////////////////
      // Publish an Outgoing Event to any connections that match the sender...
      /////////////////////////
      this.connections.values().stream()
          .filter(
              connection -> connection.getConnectorAddress().equals(message.getFromAddress())
          )
          .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
              ImmutableOutgoingMessgeRequestEvent.builder()
                  .message(message)
                  .build()
          ));

      /////////////////////////
      // Publish an Incoming Event to any connections that match the recipient...
      /////////////////////////
      this.connections.values().stream()
          .filter(
              connection -> connection.getConnectorAddress().equals(message.getToAddress())
          )
          .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
              ImmutableIncomingMessgeRequestEvent.builder()
                  .message(message)
                  .build()
          ));
    }

    /**
     * A helper function to simulate the expiration of a transfer.
     */
    public void expireTransfer(final TransferId transferId) {
      Optional.ofNullable(this.transfers.get(transferId)).ifPresent(transferHolder -> {

        /////////////////////////
        // Remove the transfer...
        /////////////////////////
        TransferHolder newTransferHolder = ImmutableTransferHolder.builder().from(transferHolder)
            .transferStatus(TransferStatus.REJECTED).build();
        this.transfers.replace(transferId, transferHolder, newTransferHolder);

        /////////////////////////
        // Publish an Outgoing Event to any connections that match the sender...
        /////////////////////////
        this.connections.values().stream()
            .filter(
                connection -> connection.getConnectorAddress()
                    .equals(newTransferHolder.getTransfer().getSourceAccount())
            )
            .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
                ImmutableOutgoingTransferCancelledEvent.builder()
                    .transfer(newTransferHolder.getTransfer())
                    .build()
            ));

        /////////////////////////
        // Publish an Incoming Event to any connections that match the recipient...
        /////////////////////////
        this.connections.values().stream()
            .filter(
                connection -> connection.getConnectorAddress()
                    .equals(newTransferHolder.getTransfer().getDestinationAccount())
            )
            .forEach(connection -> connection.getLedgerPluginEventEmitter().emitEvent(
                ImmutableIncomingTransferCancelledEvent.builder()
                    .transfer(newTransferHolder.getTransfer())
                    .build()
            ));
      });
    }

    /**
     * Reset all balances for all accounts.
     */
    public void resetBalances() {
      this.transfers.clear();
    }

    /**
     * Get the balance for a given account by cycling through all of the transfers and adding up any
     * totals for the indicated account.
     */
    public BigInteger getAccountBalance(final InterledgerAddress interledgerAddress) {
      // Prepared totals...
      final BigInteger debits = this.transfers.values().stream()
          // a debit is anything put on hold or executed from the address...
          .filter(th -> th.getTransferStatus() == TransferStatus.PREPARED
              || th.getTransferStatus() == TransferStatus.EXECUTED)
          .map(TransferHolder::getTransfer)
          .filter(tx -> tx.getSourceAccount().equals(interledgerAddress))
          .map(Transfer::getAmount)
          .reduce(BigInteger.ZERO, BigInteger::add);

      final BigInteger credits = this.transfers.values().stream()
          // a credit is anything executed to the address...
          .filter(th -> th.getTransferStatus() == TransferStatus.EXECUTED)
          .map(TransferHolder::getTransfer)
          .filter(tx -> tx.getDestinationAccount().equals(interledgerAddress))
          .map(Transfer::getAmount)
          .reduce(BigInteger.ZERO, BigInteger::add);

      // Executed totals...
      return credits.subtract(debits);
    }

    // Mock Transfer statuses...
    public enum TransferStatus {
      PREPARED,
      EXECUTED,
      REJECTED
    }

    @Value.Immutable
    public interface TransferHolder {

      Transfer getTransfer();

      @Default
      default TransferStatus getTransferStatus() {
        return TransferStatus.PREPARED;
      }

      /**
       * Will be present if the transfer is executed.
       */
      Optional<Fulfillment> getExecutionFulfillment();

      /**
       * Will be present if the transfer is rejected by the recipient (but absent if the transfer is
       * rejected mid-stream, or otherwise timed-out).
       */
      Optional<Fulfillment> getCancellationFulfillment();
    }

    @Value.Immutable
    public interface SimulatedLedgerPluginConnection {

      InterledgerAddress getConnectorAddress();

      String getPassword();

      LedgerPluginEventEmitter getLedgerPluginEventEmitter();
    }
  }
}
