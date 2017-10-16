package org.interledger.plugin.lpi.exceptions;

import org.interledger.InterledgerAddress;

/**
 * A root exception for all exceptions relating to Ledger Plugins.
 */
public class LedgerPluginException extends RuntimeException {

  /**
   * The ledger-prefix of the ledger plugin that threw this exception.
   */
  private final InterledgerAddress pluginLedgerPrefix;

  /**
   * Constructs a new runtime exception with {@code null} as its detail message.  The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   */
  public LedgerPluginException(InterledgerAddress pluginLedgerPrefix) {
    this.pluginLedgerPrefix = pluginLedgerPrefix;
  }

  /**
   * Constructs a new runtime exception with the specified detail message. The cause is not
   * initialized, and may subsequently be initialized by a call to {@link #initCause}.
   *
   * @param message the detail message. The detail message is saved for later retrieval by the
   *                {@link #getMessage()} method.
   */
  public LedgerPluginException(String message, InterledgerAddress pluginLedgerPrefix) {
    super(message);
    this.pluginLedgerPrefix = pluginLedgerPrefix;
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
  public LedgerPluginException(String message, Throwable cause,
      InterledgerAddress pluginLedgerPrefix) {
    super(message, cause);
    this.pluginLedgerPrefix = pluginLedgerPrefix;
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
  public LedgerPluginException(Throwable cause, InterledgerAddress pluginLedgerPrefix) {
    super(cause);
    this.pluginLedgerPrefix = pluginLedgerPrefix;
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
  public LedgerPluginException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace, InterledgerAddress pluginLedgerPrefix) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.pluginLedgerPrefix = pluginLedgerPrefix;
  }

  public InterledgerAddress getPluginLedgerPrefix() {
    return pluginLedgerPrefix;
  }
}
