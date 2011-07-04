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
package net.sourcewalker.garanbot.account;

import android.os.Parcel;
import android.os.Parcelable;

class LoginParcel implements Parcelable {

    public String username;
    public String password;
    public boolean successful;

    public LoginParcel(final String username, final String password,
            final boolean result) {
        this.username = username;
        this.password = password;
        this.successful = result;
    }

    public LoginParcel(final Parcel source) {
        this.username = source.readString();
        this.password = source.readString();
        this.successful = source.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
        dest.writeByte((byte) (successful ? 1 : 0));
    }

    public static final Parcelable.Creator<LoginParcel> CREATOR = new Creator<LoginParcel>() {

        @Override
        public LoginParcel[] newArray(int size) {
            return new LoginParcel[size];
        }

        @Override
        public LoginParcel createFromParcel(Parcel source) {
            return new LoginParcel(source);
        }
    };
}
