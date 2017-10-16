package org.interledger.plugin.lpi.exceptions;

import org.interledger.InterledgerAddress;
import org.interledger.plugin.lpi.TransferId;
import org.interledger.ilp.InterledgerProtocolError;

/**
 * A transfer itself was invalid in some manner as to be unacceptable to the underlying ledger. This
 * is distinct from {@link TransferNotAcceptedException}, which is used when the Transfer is fine,
 * but some other ledger-specific business logic did not allow the Transfer to be used.
 */
public class InvalidTransferException extends LedgerPluginException {

  private final TransferId transferId;
  private final InterledgerProtocolError rejectionReason;

  /**
   * Constructs a new runtime exception with {@code null} as its detail message.  The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public InvalidTransferException(InterledgerAddress sourceLedgerPrefix, TransferId transferId,
      InterledgerProtocolError rejectionReason) {
    super(sourceLedgerPrefix);
    this.transferId = transferId;
    this.rejectionReason = rejectionReason;
  }

  /**
   * Constructs a new runtime exception with the specified detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *                {@link #getMessage()} method.
   */
  public InvalidTransferException(String message, InterledgerAddress sourceLedgerPrefix,
      TransferId transferId, InterledgerProtocolError rejectionReason) {
    super(message, sourceLedgerPrefix);
    this.transferId = transferId;
    this.rejectionReason = rejectionReason;
  }

  /**
   * Constructs a new runtime exception with the specified detail message and cause.  <p>Note that
   * the detail message associated with {@code cause} is <i>not</i> automatically incorporated in
   * this runtime exception's detail message.
   *
   * @param message the detail message (which is saved for later retrieval by the {@link
   *                #getMessage()} method).
   * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()}
   *                method).  (A <tt>null</tt> value is permitted, and indicates that the cause is
   *                nonexistent or unknown.)
   *
   * @since 1.4
   */
  public InvalidTransferException(String message, Throwable cause,
      InterledgerAddress sourceLedgerPrefix, TransferId transferId,
      InterledgerProtocolError rejectionReason) {
    super(message, cause, sourceLedgerPrefix);
    this.transferId = transferId;
    this.rejectionReason = rejectionReason;
  }

  /**
   * Constructs a new runtime exception with the specified cause and a detail message of
   * <tt>(cause==null ? null : cause.toString())</tt> (which typically contains the class and detail
   * message of <tt>cause</tt>).  This constructor is useful for runtime exceptions that are little
   * more than wrappers for other throwables.
   *
   * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
   *              (A <tt>null</tt> value is permitted, and indicates that the cause is nonexistent
   *              or unknown.)
   *
   * @since 1.4
   */
  public InvalidTransferException(Throwable cause, InterledgerAddress sourceLedgerPrefix,
      TransferId transferId, InterledgerProtocolError rejectionReason) {
    super(cause, sourceLedgerPrefix);
    this.transferId = transferId;
    this.rejectionReason = rejectionReason;
  }

  /**
   * Constructs a new runtime exception with the specified detail message, cause, suppression
   * enabled or disabled, and writable stack trace enabled or disabled.
   *
   * @param message            the detail message.
   * @param cause              the cause.  (A {@code null} value is permitted, and indicates that
   *                           the cause is nonexistent or unknown.)
   * @param enableSuppression  whether or not suppression is enabled or disabled
   * @param writableStackTrace whether or not the stack trace should be writable
   *
   * @since 1.7
   */
  public InvalidTransferException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace, InterledgerAddress sourceLedgerPrefix,
      TransferId transferId, InterledgerProtocolError rejectionReason) {
    super(message, cause, enableSuppression, writableStackTrace, sourceLedgerPrefix);
    this.transferId = transferId;
    this.rejectionReason = rejectionReason;
  }

  public TransferId getTransferId() {
    return transferId;
  }

  public InterledgerProtocolError getRejectionReason() {
    return rejectionReason;
  }
}
