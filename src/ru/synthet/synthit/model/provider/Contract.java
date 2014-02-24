package ru.synthet.synthit.model.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
	public static final String AUTHORITY = "ru.synthet.synthit";
	
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	
	public interface UsersCoulmns {
        public static final String DN = "dn";
		public static final String UID = "uid";
		public static final String DISPLAY_NAME = "displayName";
        public static final String DESCRIPTION = "description";
        public static final String PASSWORD = "password";
        public static final String PASSWORD_AD = "password_ad";

	}

    public interface CompsCoulmns {
        public static final String DN = "dn";
        public static final String UID = "uid";
        public static final String DISPLAY_NAME = "displayName";
        public static final String DESCRIPTION = "description";
        public static final String PASSWORD = "password";
        public static final String PASSWORD_AD = "password_ad";
    }

	public static final class Users implements BaseColumns, UsersCoulmns {
		public static final String CONTENT_PATH = "users";
		public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + CONTENT_PATH;
	}

    public static final class Comps implements BaseColumns, CompsCoulmns {
        public static final String CONTENT_PATH = "comps";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_PATH);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + CONTENT_PATH;
    }
}
