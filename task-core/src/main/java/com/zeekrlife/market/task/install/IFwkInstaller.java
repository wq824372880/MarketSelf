package com.zeekrlife.market.task.install;

import androidx.annotation.NonNull;

import com.zeekrlife.market.task.bean.PackagesBean;

public interface IFwkInstaller {
    boolean preVerify(@NonNull String filePath, @NonNull String hash);

    String unzip(@NonNull String zipFilePath);

    PackagesBean verify(@NonNull String unzipDirPath);

    boolean verifySystem();

    boolean install(@NonNull String unzipDirPath, @NonNull PackagesBean packagesBean);
}
