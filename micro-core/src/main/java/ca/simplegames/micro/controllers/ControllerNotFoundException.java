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

package ca.simplegames.micro.controllers;

/**
 * ControllerNotFoundException
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-19 4:01 PM)
 */
public class ControllerNotFoundException extends Exception {
    /**
     * Constructor for ControllerNotFoundException.
     */
    public ControllerNotFoundException() {
        super();
    }

    /**
     * Constructor for ControllerNotFoundException.
     *
     * @param message
     */
    public ControllerNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor for ControllerNotFoundException.
     *
     * @param message
     * @param cause
     */
    public ControllerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor for ControllerNotFoundException.
     *
     * @param cause
     */
    public ControllerNotFoundException(Throwable cause) {
        super(cause);
    }
}
