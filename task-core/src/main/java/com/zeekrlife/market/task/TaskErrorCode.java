package com.zeekrlife.market.task;

public interface TaskErrorCode {
    int PRE_DOWNLOAD_CHECK_FAILURE = -101;

    int DOWNLOAD_FAILURE_DEFAULT = -201;

    int DOWNLOAD_FAILURE_BY_NET_ERROR = -210;

    int PRE_INSTALL_FAILURE_CHECK_HASH_FAILED = -301;

    int INSTALL_FAILURE_DEFAULT = -401;

    int INSTALL_FAILURE_URL_ILLEGAL = -402;

    int INSTALL_NUMBER_LIMITED = -501;
}
