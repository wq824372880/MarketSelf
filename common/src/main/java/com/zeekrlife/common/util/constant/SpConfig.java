package com.zeekrlife.common.util.constant;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface SpConfig {
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            StringKey.SEARCH_HISTORIES,
            StringKey.USER_ID,
            StringKey.VIN_ID,

    })
    @interface StringKey {
        String SEARCH_HISTORIES = "search_histories";
        String USER_ID = "user_id";
        String VIN_ID = "vin_id";
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            StringSetKey.AUTO_UPDATE_ID_SET,
            StringSetKey.LOCAL_TITLE_SET,

    })
    @interface StringSetKey {
        String AUTO_UPDATE_ID_SET = "auto_update_id_set";
        String LOCAL_TITLE_SET = "local_title_set";
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({

    })
    @interface IntKey {

    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({

    })
    @interface LongKey {

    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({

    })
    @interface FloatKey {

    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            BooleanKey.HAS_AGREED_AGREEMENT,
            BooleanKey.AUTO_UPDATE,

    })
    @interface BooleanKey {
        String HAS_AGREED_AGREEMENT = "has_agreed_agreement";
        String AUTO_UPDATE = "auto_update";
    }
}
