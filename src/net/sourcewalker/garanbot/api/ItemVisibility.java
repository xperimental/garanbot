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
 * Enum containing all the possible values for the item's visibility to friends.
 * 
 * @author Xperimental
 */
public enum ItemVisibility {
    PRIVATE(0), FRIENDS(1), FRIENDS_OF_FRIENDS(2), PUBLIC(3);

    private int value;

    ItemVisibility(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ItemVisibility parseInt(int value) {
        for (ItemVisibility iv : values()) {
            if (iv.getValue() == value) {
                return iv;
            }
        }
        throw new IllegalArgumentException("ItemVisibility value not found: "
                + value);
    }
}
