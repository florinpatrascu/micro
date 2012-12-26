/*
 * Copyright (c)2012. Florin T.PATRASCU
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.simplegames.micro.helpers.i18n;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.text.MessageFormat;
import java.util.*;


/**
 * Abstract implementation of the {@link HierarchicalMessageSource} interface,
 * implementing common handling of message variants, making it easy
 * to implement a specific strategy for a concrete MessageSource.
 * <p/>
 * <p>Subclasses must implement the abstract {@link #resolveCode}
 * method. For efficient resolution of messages without arguments, the
 * {@link #resolveCodeWithoutArguments} method should be overridden
 * as well, resolving messages without a MessageFormat being involved.
 * <p/>
 * <p><b>Note:</b> By default, message texts are only parsed through
 * MessageFormat if arguments have been passed in for the message. In case
 * of no arguments, message texts will be returned as-is. As a consequence,
 * you should only use MessageFormat escaping for messages with actual
 * arguments, and keep all other messages unescaped. If you prefer to
 * escape all messages, set the "alwaysUseMessageFormat" flag to "true".
 * <p/>
 * <p>Supports not only MessageSourceResolvables as primary messages
 * but also resolution of message arguments that are in turn
 * MessageSourceResolvables themselves.
 * <p/>
 * <p>This class does not implement caching of messages per code, thus
 * subclasses can dynamically change messages over time. Subclasses are
 * encouraged to cache their messages in a modification-aware fashion,
 * allowing for hot deployment of updated messages.
 *
 * @author Juergen Hoeller
 * @author Rod Johnson
 * @see #resolveCode(String, java.util.Locale)
 * @see #resolveCodeWithoutArguments(String, java.util.Locale)
 * @see #setAlwaysUseMessageFormat
 * @see java.text.MessageFormat
 */
public abstract class AbstractMessageSource implements HierarchicalMessageSource {

    /**
     * Logger available to subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    private MessageSource parentMessageSource;

    private boolean useCodeAsDefaultMessage = false;

    private boolean alwaysUseMessageFormat = false;

    /**
     * Cache to hold already generated MessageFormats per message.
     * Used for passed-in default messages. MessageFormats for resolved
     * codes are cached on a specific basis in subclasses.
     */
    private final Map cachedMessageFormats = new HashMap();


    public void setParentMessageSource(MessageSource parent) {
        this.parentMessageSource = parent;
    }

    public MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }

    /**
     * Set whether to use the message code as default message instead of
     * throwing a NoSuchMessageException. Useful for development and debugging.
     * Default is "false".
     * <p>Note: In case of a MessageSourceResolvable with multiple codes
     * (like a FieldError) and a MessageSource that has a parent MessageSource,
     * do <i>not</i> activate "useCodeAsDefaultMessage" in the <i>parent</i>:
     * Else, you'll get the first code returned as message by the parent,
     * without attempts to check further codes.
     * <p>To be able to work with "useCodeAsDefaultMessage" turned on in the parent,
     * AbstractMessageSource and AbstractApplicationContext contain special checks
     * to delegate to the internal <code>getMessageInternal</code> method if available.
     * In general, it is recommended to just use "useCodeAsDefaultMessage" during
     * development and not rely on it in production in the first place, though.
     *
     * @see #getMessage(String, Object[], Locale)
     * @see #getMessageInternal
     */
    public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
    }

    /**
     * Return whether to use the message code as default message instead of
     * throwing a NoSuchMessageException. Useful for development and debugging.
     * Default is "false".
     * <p>Alternatively, consider overriding the <code>getDefaultMessage</code>
     * method to return a custom fallback message for an unresolvable code.
     *
     * @see #getDefaultMessage(String)
     */
    protected boolean isUseCodeAsDefaultMessage() {
        return this.useCodeAsDefaultMessage;
    }

    /**
     * Set whether to always apply the MessageFormat rules, parsing even
     * messages without arguments.
     * <p>Default is "false": Messages without arguments are by default
     * returned as-is, without parsing them through MessageFormat.
     * Set this to "true" to enforce MessageFormat for all messages,
     * expecting all message texts to be written with MessageFormat escaping.
     * <p>For example, MessageFormat expects a single quote to be escaped
     * as "''". If your message texts are all written with such escaping,
     * even when not defining argument placeholders, you need to set this
     * flag to "true". Else, only message texts with actual arguments
     * are supposed to be written with MessageFormat escaping.
     *
     * @see java.text.MessageFormat
     */
    public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
        this.alwaysUseMessageFormat = alwaysUseMessageFormat;
    }

    /**
     * Return whether to always apply the MessageFormat rules, parsing even
     * messages without arguments.
     */
    protected boolean isAlwaysUseMessageFormat() {
        return this.alwaysUseMessageFormat;
    }


    public final String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        String msg = getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        if (defaultMessage == null) {
            String fallback = getDefaultMessage(code);
            if (fallback != null) {
                return fallback;
            }
        }
        return renderDefaultMessage(defaultMessage, args, locale);
    }

    public final String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        String msg = getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        String fallback = getDefaultMessage(code);
        if (fallback != null) {
            return fallback;
        }
        throw new NoSuchMessageException(code, locale);
    }

    public final String getMessage(MessageSourceResolvable resolvable, Locale locale)
            throws NoSuchMessageException {

        String[] codes = resolvable.getCodes();
        if (codes == null) {
            codes = new String[0];
        }
        for (int i = 0; i < codes.length; i++) {
            String msg = getMessageInternal(codes[i], resolvable.getArguments(), locale);
            if (msg != null) {
                return msg;
            }
        }
        if (resolvable.getDefaultMessage() != null) {
            return renderDefaultMessage(resolvable.getDefaultMessage(), resolvable.getArguments(), locale);
        }
        if (codes.length > 0) {
            String fallback = getDefaultMessage(codes[0]);
            if (fallback != null) {
                return fallback;
            }
        }
        throw new NoSuchMessageException(codes.length > 0 ? codes[codes.length - 1] : null, locale);
    }


    /**
     * Resolve the given code and arguments as message in the given Locale,
     * returning null if not found. Does <i>not</i> fall back to the code
     * as default message. Invoked by getMessage methods.
     *
     * @param code   the code to lookup up, such as 'calculator.noRateSet'
     * @param args   array of arguments that will be filled in for params
     *               within the message
     * @param locale the Locale in which to do the lookup
     * @return the resolved message, or <code>null</code> if not found
     * @see #getMessage(String, Object[], String, Locale)
     * @see #getMessage(String, Object[], Locale)
     * @see #getMessage(MessageSourceResolvable, Locale)
     * @see #setUseCodeAsDefaultMessage
     */
    protected String getMessageInternal(String code, Object[] args, Locale locale) {
        if (code == null) {
            return null;
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Object[] argsToUse = args;

        if (!isAlwaysUseMessageFormat() && (args == null || args.length == 0)) {
            // Optimized resolution: no arguments to apply,
            // therefore no MessageFormat needs to be involved.
            // Note that the default implementation still uses MessageFormat;
            // this can be overridden in specific subclasses.
            String message = resolveCodeWithoutArguments(code, locale);
            if (message != null) {
                return message;
            }
        } else {
            // Resolve arguments eagerly, for the case where the message
            // is defined in a parent MessageSource but resolvable arguments
            // are defined in the child MessageSource.
            argsToUse = resolveArguments(args, locale);

            MessageFormat messageFormat = resolveCode(code, locale);
            if (messageFormat != null) {
                synchronized (messageFormat) {
                    return messageFormat.format(argsToUse);
                }
            }
        }

        // Not found -> check parent, if any.
        return getMessageFromParent(code, argsToUse, locale);
    }

    /**
     * Try to retrieve the given message from the parent MessageSource, if any.
     *
     * @param code   the code to lookup up, such as 'calculator.noRateSet'
     * @param args   array of arguments that will be filled in for params
     *               within the message
     * @param locale the Locale in which to do the lookup
     * @return the resolved message, or <code>null</code> if not found
     * @see #getParentMessageSource()
     */
    protected String getMessageFromParent(String code, Object[] args, Locale locale) {
        MessageSource parent = getParentMessageSource();
        if (parent != null) {
            if (parent instanceof AbstractMessageSource) {
                // Call internal method to avoid getting the default code back
                // in case of "useCodeAsDefaultMessage" being activated.
                return ((AbstractMessageSource) parent).getMessageInternal(code, args, locale);
            } else {
                // Check parent MessageSource, returning null if not found there.
                return parent.getMessage(code, args, null, locale);
            }
        }
        // Not found in parent either.
        return null;
    }

    /**
     * Return a fallback default message for the given code, if any.
     * <p>Default is to return the code itself if "useCodeAsDefaultMessage"
     * is activated, or return no fallback else. In case of no fallback,
     * the caller will usually receive a NoSuchMessageException from
     * <code>getMessage</code>.
     *
     * @param code the message code that we couldn't resolve
     *             and that we didn't receive an explicit default message for
     * @return the default message to use, or <code>null</code> if none
     * @see #setUseCodeAsDefaultMessage
     */
    protected String getDefaultMessage(String code) {
        if (isUseCodeAsDefaultMessage()) {
            return code;
        }
        return null;
    }


    /**
     * Render the given default message String. The default message is
     * passed in as specified by the caller and can be rendered into
     * a fully formatted default message shown to the user.
     * <p>Default implementation passes the String to <code>formatMessage</code>,
     * resolving any argument placeholders found in them. Subclasses may override
     * this method to plug in custom processing of default messages.
     *
     * @param defaultMessage the passed-in default message String
     * @param args           array of arguments that will be filled in for params within
     *                       the message, or <code>null</code> if none.
     * @param locale         the Locale used for formatting
     * @return the rendered default message (with resolved arguments)
     * @see #formatMessage(String, Object[], java.util.Locale)
     */
    protected String renderDefaultMessage(String defaultMessage, Object[] args, Locale locale) {
        return formatMessage(defaultMessage, args, locale);
    }

    /**
     * Format the given message String, using cached MessageFormats.
     * By default invoked for passed-in default messages, to resolve
     * any argument placeholders found in them.
     *
     * @param msg    the message to format
     * @param args   array of arguments that will be filled in for params within
     *               the message, or <code>null</code> if none.
     * @param locale the Locale used for formatting
     * @return the formatted message (with resolved arguments)
     */
    protected String formatMessage(String msg, Object[] args, Locale locale) {
        if (msg == null || (!this.alwaysUseMessageFormat && (args == null || args.length == 0))) {
            return msg;
        }
        MessageFormat messageFormat = null;
        synchronized (this.cachedMessageFormats) {
            messageFormat = (MessageFormat) this.cachedMessageFormats.get(msg);
            if (messageFormat == null) {
                messageFormat = createMessageFormat(msg, locale);
                this.cachedMessageFormats.put(msg, messageFormat);
            }
        }
        synchronized (messageFormat) {
            return messageFormat.format(resolveArguments(args, locale));
        }
    }

    /**
     * Create a MessageFormat for the given message and Locale.
     * <p>This implementation creates an empty MessageFormat first,
     * populating it with Locale and pattern afterwards, to stay
     * compatible with J2SE 1.3.
     *
     * @param msg    the message to create a MessageFormat for
     * @param locale the Locale to create a MessageFormat for
     * @return the MessageFormat instance
     */
    protected MessageFormat createMessageFormat(String msg, Locale locale) {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating MessageFormat for pattern [" + msg + "] and locale '" + locale + "'");
        }
        MessageFormat messageFormat = new MessageFormat("");
        messageFormat.setLocale(locale);
        if (msg != null) {
            messageFormat.applyPattern(msg);
        }
        return messageFormat;
    }


    /**
     * Search through the given array of objects, find any
     * MessageSourceResolvable objects and resolve them.
     * <p>Allows for messages to have MessageSourceResolvables as arguments.
     *
     * @param args   array of arguments for a message
     * @param locale the locale to resolve through
     * @return an array of arguments with any MessageSourceResolvables resolved
     */
    protected Object[] resolveArguments(Object[] args, Locale locale) {
        if (args == null) {
            return new Object[0];
        }
        List resolvedArgs = new ArrayList(args.length);
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof MessageSourceResolvable) {
                resolvedArgs.add(getMessage((MessageSourceResolvable) args[i], locale));
            } else {
                resolvedArgs.add(args[i]);
            }
        }
        return resolvedArgs.toArray(new Object[resolvedArgs.size()]);
    }

    /**
     * Subclasses can override this method to resolve a message without
     * arguments in an optimized fashion, that is, to resolve a message
     * without involving a MessageFormat.
     * <p>The default implementation <i>does</i> use MessageFormat,
     * through delegating to the <code>resolveCode</code> method.
     * Subclasses are encouraged to replace this with optimized resolution.
     * <p>Unfortunately, <code>java.text.MessageFormat</code> is not
     * implemented in an efficient fashion. In particular, it does not
     * detect that a message pattern doesn't contain argument placeholders
     * in the first place. Therefore, it's advisable to circumvent
     * MessageFormat completely for messages without arguments.
     *
     * @param code   the code of the message to resolve
     * @param locale the Locale to resolve the code for
     *               (subclasses are encouraged to support internationalization)
     * @return the message String, or <code>null</code> if not found
     * @see #resolveCode
     * @see java.text.MessageFormat
     */
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        MessageFormat messageFormat = resolveCode(code, locale);
        if (messageFormat != null) {
            synchronized (messageFormat) {
                return messageFormat.format(new Object[0]);
            }
        }
        return null;
    }

    /**
     * Subclasses must implement this method to resolve a message.
     * <p>Returns a MessageFormat instance rather than a message String,
     * to allow for appropriate caching of MessageFormats in subclasses.
     * <p><b>Subclasses are encouraged to provide optimized resolution
     * for messages without arguments, not involving MessageFormat.</b>
     * See <code>resolveCodeWithoutArguments</code> javadoc for details.
     *
     * @param code   the code of the message to resolve
     * @param locale the Locale to resolve the code for
     *               (subclasses are encouraged to support internationalization)
     * @return the MessageFormat for the message, or <code>null</code> if not found
     * @see #resolveCodeWithoutArguments(String, java.util.Locale)
     */
	protected abstract MessageFormat resolveCode(String code, Locale locale);

}
