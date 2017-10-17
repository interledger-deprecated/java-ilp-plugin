package org.interledger.plugin.lpi;

import org.interledger.InterledgerAddress;
import org.interledger.cryptoconditions.Condition;
import org.interledger.cryptoconditions.Fulfillment;
import org.interledger.ilp.InterledgerProtocolError;
import org.interledger.plugin.lpi.exceptions.AccountNotFoundException;
import org.interledger.plugin.lpi.exceptions.DuplicateTransferIdentifier;
import org.interledger.plugin.lpi.exceptions.InsufficientBalanceException;
import org.interledger.plugin.lpi.exceptions.InvalidFulfillmentException;
import org.interledger.plugin.lpi.exceptions.InvalidMessageException;
import org.interledger.plugin.lpi.exceptions.InvalidTransferException;
import org.interledger.plugin.lpi.exceptions.LedgerPluginException;
import org.interledger.plugin.lpi.exceptions.LedgerPluginNotConnectedException;
import org.interledger.plugin.lpi.exceptions.MessageNotAcceptedException;
import org.interledger.plugin.lpi.exceptions.TransferAlreadyFulfilledException;
import org.interledger.plugin.lpi.exceptions.TransferAlreadyRolledBackException;
import org.interledger.plugin.lpi.exceptions.TransferNotAcceptedException;
import org.interledger.plugin.lpi.exceptions.TransferNotFoundException;
import org.interledger.plugin.lpi.handlers.LedgerPluginEventHandler;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines an abstraction that is meant to be plugged-in to a Connector or other ILP system in order
 * for it to communicate with an underlying ledger in a consistent fashion.
 *
 * Per IL-RFC-4, the general contract for a ledger plugin is that, for a single ledger, it can
 * prepare transfers from a Connector account on that ledger to a destination account on the same
 * ledger. Additionally, ledger plugins can also emit events received from an underlying ledger
 * relating to various local-ledger transfers on that underlying ledger, such as "prepare" and
 * "fulfill" events, in addition to ILQP quoting and other messaging.
 *
 * Architecturally, an ILP system like a Connector may have _many_ ledger plugin implementations
 * that it interfaces with, but only a single ledger plugin may be operational for a given ledger
 * prefix, which is an {@link InterledgerAddress}.
 *
 * The following high-level component diagram illustrates how ledger plugins are used by a
 * connector:
 *
 * <pre>
 * Events/Messages──────────────┬────────────────Events/Messages
 *     │                        │                       │
 *     │                        ▼                       │
 *     │         ┌────────────────────────────┐         │
 *     │     ┌───│         Connector          │───┐     │
 *     │     │   └────────────────────────────┘   │     │
 *     │     │                  │                 │     │
 *     │  g.usd.                │              g.jpy.   │
 *     │     │               g.eur.               │     │
 *     │     │                  │                 │     │
 *     │     │                  │                 │     │
 *     │     ▽                  ▽                 ▽     │
 *     │  ┌─────┐            ┌─────┐           ┌─────┐  │
 *     └──│LPI1 │            │LPI2 │           │LPI3 │──┘
 *        └─────┘            └─────┘           └─────┘
 *           △                  △                 △
 *           │                  │                 │
 *           ▽                  ▽                 ▽
 *      ┌──────────┐       ┌──────────┐      ┌──────────┐
 *      │ Ledger 1 │       │ Ledger 2 │      │ Ledger 3 │
 *      └──────────┘       └──────────┘      └──────────┘
 * </pre>
 *
 * Note that for some implementations, there may only be a single account on the underlying ledger,
 * in which case it is likely that the Bilateral Transfer Protocol (BTP) implementation will be used
 * as an implementation of a {@link LedgerPlugin}.
 *
 * @see "https://github.com/interledger/rfcs/blob/master/0004-ledger-plugin-interface/
 *     0004-ledger-plugin-interface.md"
 */
public interface LedgerPlugin extends Plugin {

  /**
   * Retrieve some metadata about the ledger.
   *
   * @return A {@link LedgerInfo} with information about the underlying ledger.
   *
   * @throws LedgerPluginNotConnectedException if the plugin is not connected.
   */
  LedgerInfo getLedgerInfo();

  /**
   * Get the ledger plugin's ILP address that this Connector uses on the underlying ledger (running
   * behind this ledger plugin). This is given to senders to receive transfers to this account.
   * Plugin must be connected, otherwise the function should throw.
   *
   * The mapping from the ILP address to the local ledger address is dependent on the ledger /
   * ledger plugin. An ILP address could be the <ledger prefix>.<account name or number>, or a token
   * could be used in place of the actual account name or number.
   *
   * @throws LedgerPluginNotConnectedException if the plugin is not connected.
   */
  InterledgerAddress getConnectorAccount();

  /**
   * Return a (base-ten) integer string (..., '-3', '-2', '-1', '0', '1', '2', '3', ...)
   * representing the current balance, in the ledger's base unit. For example, on a ledger with
   * currencyCode 'USD' and currencyScale 6, the base unit would be micro-dollars. A balance of
   * '1230000' should then be interpreted as equivalent to 1.23 US dollars.
   *
   * @throws LedgerPluginNotConnectedException if the plugin is not connected.
   */
  BigInteger getConnectorBalance();

  /**
   * Called to initiate ledger event subscriptions. Once this method is called, the ledger plugin
   * MUST attempt to subscribe to and report ledger events, including the "connect" event
   * immediately after a successful connection is established. If the connection is lost, the ledger
   * plugin SHOULD emit the "disconnect" event.
   *
   * This plugin should ensure that the information returned by {@link #getLedgerInfo()} and {@link
   * #getConnectorAccount()} is available and cached before emitting the connect event.
   *
   * @throws LedgerPluginNotConnectedException if invalid credentials are missing or otherwise
   *                                           invalid.
   * @throws LedgerPluginException             if {@link LedgerPluginConfig#getOptions()} has a
   *                                           "timeout" value that is non-numeric or otherwise
   *                                           invalid for the implementation.
   */
  void connect();

  /**
   * Called to disconnect this ledger plugin from the underlying ledger.
   *
   * @throws LedgerPluginNotConnectedException if invalid credentials are missing or otherwise
   *                                           invalid.
   * @throws LedgerPluginException             if {@link LedgerPluginConfig#getOptions()} has a
   *                                           "timeout" value that is non-numeric or otherwise
   *                                           invalid for the implementation.
   */
  void disconnect();

  /**
   * Query whether the plugin is currently connected.
   *
   * @return {@code true} if the plugin is connected, {@code false} otherwise.
   */
  boolean isConnected();

  /**
   * Return the optionally-present fulfillment for a conditional transfer, if it has already been
   * executed, or {@link Optional#empty()} if the transfer has not yet been fulfilled.
   *
   * @param transferId A {@link TransferId} that uniquely identifies the transfer to get a
   *                   fulfillment for.
   *
   * @return An optionally-present {@link Fulfillment}, or {@link Optional#empty()} if the transfer
   *     has not yet been fulfilled.
   *
   * @throws LedgerPluginNotConnectedException  if the plugin is not connected.
   * @throws TransferNotFoundException          if no conditional transfer is found with the given
   *                                            {@code transferId}.
   * @throws TransferAlreadyRolledBackException if the transfer has been rolled back and will not be
   *                                            fulfilled.
   */
  Optional<Fulfillment> getFulfillment(TransferId transferId);

  /**
   * Initiates a ledger-local transfer. A transfer can contain money and/or information. If there is
   * a problem with the structure or validity of the transfer, then this method will throw an
   * exception. However, if the transfer is accepted by the ledger, then further errors will be in
   * the form of "reject" events.
   *
   * Note that _all_ plugins MUST implement zero-amount transfers, but some ledger plugins MAY
   * implement zero-amount transfers differently than other transfers.
   *
   * @param transfer A {@link Transfer} to prepare on the underlying ledger.
   *
   * @throws LedgerPluginNotConnectedException if the plugin is not connected.
   * @throws InvalidTransferException          if required fields are missing from the transfer or
   *                                           the transfer is otherwise malformed or invalid.
   * @throws DuplicateTransferIdentifier       if a transfer with the given ID and different data
   *                                           already exists.
   * @throws InsufficientBalanceException      if the transfer is rejected due to the source balance
   *                                           being too low.
   * @throws AccountNotFoundException          if the destination account does not exist.
   * @throws TransferNotAcceptedException      if the transfer is otherwise rejected by the ledger
   *                                           due to ledger-side business logic.
   */
  void sendTransfer(Transfer transfer);

  /**
   * Sends a ledger-local message, for example for quoting and broadcasting routes.
   *
   * @param message A {@link Message} to send to the underlying ledger.
   *
   * @throws LedgerPluginNotConnectedException if the plugin is not connected.
   * @throws InvalidMessageException           if required fields are missing from the message or
   *                                           the message is otherwise malformed or invalid.
   * @throws AccountNotFoundException          if the destination account does not exist.
   * @throws MessageNotAcceptedException       if the message is otherwise rejected by the ledger
   *                                           due to ledger-side business logic. TODO: Should there
   *                                           be a NoSubscriptionsError?
   */
  void sendMessage(Message message);

  /**
   * Submit a fulfillment to a ledger in order to instruct it to execute a prepared transfer.
   *
   * @param transferId  A {@link TransferId} that uniquely identifies the transfer fulfill.
   * @param fulfillment A {@link Fulfillment} that should fulfill a previously submitted {@link
   *                    Condition} for the specified {@code transferId}.
   *
   * @throws LedgerPluginNotConnectedException  if the plugin is not connected.
   * @throws TransferNotFoundException          if no conditional transfer is found with the given
   *                                            {@code transferId}.
   * @throws TransferAlreadyRolledBackException if the transfer has already been rolled back.
   * @throws InvalidFulfillmentException        if {@code fulfillment} is malformed, or, if the
   *                                            fulfillment is formatted correctly, but does not
   *                                            match the condition of the specified transfer.
   */
  void fulfillCondition(TransferId transferId, Fulfillment fulfillment);

  /**
   * Reject an incoming transfer that is held pending the fulfillment of its {@link
   * Transfer#getExecutionCondition()} before the {@link Transfer#getExpiresAt()} time.
   *
   *
   *
   * This MAY be used by receivers or connectors to reject incoming funds if they will not fulfill
   * the condition or are unable to forward the payment. Previous hops in an Interledger transfer
   * would have their money returned before the expiry and the sender or previous connectors MAY
   * retry and reroute the transfer through an alternate path.
   *
   * @param transferId      A {@link TransferId} that uniquely identifies the transfer reject.
   * @param rejectionReason A {@link InterledgerProtocolError} that provides additional information
   *                        about the rejection.
   *
   * @throws LedgerPluginNotConnectedException if the plugin is not connected.
   * @throws TransferNotFoundException         if no conditional transfer is found with the given
   *                                           {@code transferId}.
   * @throws TransferAlreadyFulfilledException if the transfer has already been rolled
   *                                           back/cancelled.
   *
   *                                           Rejects with NotAcceptedError if you are not
   *                                           authorized to reject the transfer (e.g. if you are
   *                                           the sender).
   */
  void rejectIncomingTransfer(TransferId transferId, InterledgerProtocolError rejectionReason);

  /**
   * Add a ledger plugin event handler to this plugin.
   *
   * Care should be taken when adding multiple handlers to ensure that they perform distinct
   * operations, otherwise duplicate functionality might be unintentionally introduced.
   *
   * @param eventHandler A {@link LedgerPluginEventHandler} that can handle various types of events
   *                     emitted by this ledger plugin.
   *
   * @return A {@link UUID} representing the unique identifier of the handler, as seen by this
   *     ledger plugin.
   */
  UUID addLedgerPluginEventHandler(LedgerPluginEventHandler eventHandler);

  /**
   * Removes an event handler from the collection of handlers registered with this ledger plugin.
   *
   * @param eventHandlerId A {@link UUID} representing the unique identifier of the handler, as seen
   *                       by this ledger plugin.
   */
  void removeLedgerPluginEventHandler(UUID eventHandlerId);

  /**
   * Accessor the emitter so that external actors can emit events to this plugin.
   */
  LedgerPluginEventEmitter getLedgerPluginEventEmitter();
}
