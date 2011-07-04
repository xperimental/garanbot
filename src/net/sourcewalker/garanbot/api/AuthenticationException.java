//
// Copyright 2011 Thomas Gumprecht, Robert Jacob, Thomas Pieronczyk
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package net.sourcewalker.garanbot.api;

/**
 * Specialized exception which signals a authentication error.
 * 
 * @author Xperimental
 */
public class AuthenticationException extends ClientException {

    private static final long serialVersionUID = 2991673478702964837L;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AuthenticationException(String detailMessage) {
        super(detailMessage);
    }

    public AuthenticationException(Throwable throwable) {
        super(throwable);
    }

}
