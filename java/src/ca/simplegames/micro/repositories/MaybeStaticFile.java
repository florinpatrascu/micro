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

package ca.simplegames.micro.repositories;

/**
 * MaybeStaticFile
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-22 6:49 PM)
 */
public class MaybeStaticFile extends Exception {
    /**
     * Constructor for MaybeStaticFile.
     */
    public MaybeStaticFile() {
        super();
    }

    /**
     * Constructor for MaybeStaticFile.
     *
     * @param message
     */
    public MaybeStaticFile(String message) {
        super(message);
    }

    /**
     * Constructor for MaybeStaticFile.
     *
     * @param message
     * @param cause
     */
    public MaybeStaticFile(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for MaybeStaticFile.
     *
     * @param cause
     */
    public MaybeStaticFile(Throwable cause) {
        super(cause);
    }
}
