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

package ca.simplegames.micro.extensions.i18n;

import java.util.Locale;

/**
 * Simple holder class that associates a LocaleContext instance
 * with the current thread. The LocaleContext will be inherited
 * by any child threads spawned by the current thread.
 *
 * <p>Used as a central holder for the current Locale in Spring,
 * wherever necessary: for example, in MessageSourceAccessor.
 * DispatcherServlet automatically exposes its current Locale here.
 * Other applications can expose theirs too, to make classes like
 * MessageSourceAccessor automatically use that Locale.
 *
 * @author Juergen Hoeller
 * @since 1.2
 * @see LocaleContext
 * @see org.springframework.context.support.MessageSourceAccessor
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public abstract class LocaleContextHolder {

	private static final ThreadLocal localeContextHolder = new ThreadLocal();

	private static final ThreadLocal inheritableLocaleContextHolder = new InheritableThreadLocal();


	/**
	 * Reset the LocaleContext for the current thread.
	 */
	public static void resetLocaleContext() {
		localeContextHolder.set(null);
		inheritableLocaleContextHolder.set(null);
	}

	/**
	 * Associate the given LocaleContext with the current thread,
	 * <i>not</i> exposing it as inheritable for child threads.
	 * @param localeContext the current LocaleContext, or <code>null</code> to reset
	 * the thread-bound context
	 */
	public static void setLocaleContext(LocaleContext localeContext) {
		setLocaleContext(localeContext, false);
	}

	/**
	 * Associate the given LocaleContext with the current thread.
	 * @param localeContext the current LocaleContext, or <code>null</code> to reset
	 * the thread-bound context
	 * @param inheritable whether to expose the LocaleContext as inheritable
	 * for child threads (using an {@link java.lang.InheritableThreadLocal})
	 */
	public static void setLocaleContext(LocaleContext localeContext, boolean inheritable) {
		if (inheritable) {
			inheritableLocaleContextHolder.set(localeContext);
			localeContextHolder.set(null);
		}
		else {
			localeContextHolder.set(localeContext);
			inheritableLocaleContextHolder.set(null);
		}
	}

	/**
	 * Return the LocaleContext associated with the current thread, if any.
	 * @return the current LocaleContext, or <code>null</code> if none
	 */
	public static LocaleContext getLocaleContext() {
		LocaleContext localeContext = (LocaleContext) localeContextHolder.get();
		if (localeContext == null) {
			localeContext = (LocaleContext) inheritableLocaleContextHolder.get();
		}
		return localeContext;
	}

	/**
	 * Associate the given Locale with the current thread.
	 * <p>Will implicitly create a LocaleContext for the given Locale,
	 * <i>not</i> exposing it as inheritable for child threads.
	 * @param locale the current Locale, or <code>null</code> to reset
	 * the thread-bound context
	 */
	public static void setLocale(Locale locale) {
		setLocale(locale, false);
	}

	/**
	 * Associate the given Locale with the current thread.
	 * <p>Will implicitly create a LocaleContext for the given Locale.
	 * @param locale the current Locale, or <code>null</code> to reset
	 * the thread-bound context
	 * @param inheritable whether to expose the LocaleContext as inheritable
	 * for child threads (using an {@link java.lang.InheritableThreadLocal})
	 */
	public static void setLocale(Locale locale, boolean inheritable) {
		LocaleContext localeContext = (locale != null ? new SimpleLocaleContext(locale) : null);
		setLocaleContext(localeContext, inheritable);
	}

	/**
	 * Return the Locale associated with the current thread, if any,
	 * or the system default Locale else.
	 * @return the current Locale, or the system default Locale if no
	 * specific Locale has been associated with the current thread
	 * @see LocaleContext#getLocale()
	 * @see java.util.Locale#getDefault()
	 */
	public static Locale getLocale() {
		LocaleContext localeContext = getLocaleContext();
		return (localeContext != null ? localeContext.getLocale() : Locale.getDefault());
	}

}
