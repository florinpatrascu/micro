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

import java.util.Locale;

/**
 * Strategy interface for determining the current Locale.
 * <p/>
 * <p>A LocaleContext instance can be associated with a thread
 * via the LocaleContextHolder class.
 *
 * @author Juergen Hoeller
 * @see LocaleContextHolder
 * @see java.util.Locale
 * @since 1.2
 */
public interface LocaleContext {

    /**
     * Return the current Locale, which can be fixed or determined dynamically,
     * depending on the implementation strategy.
     */
    Locale getLocale();

}
