/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.util;

import static nil.nadph.qnotified.util.Utils.log;

import java.io.IOException;
import me.singleneuron.qn_kernel.data.HostInfo;
import nil.nadph.qnotified.config.ConfigManager;

public class LicenseStatus {

    public static final String qn_eula_status = "qh_eula_status";//typo, ignore it
    public static final String qn_user_auth_status = "qn_user_auth_status";
    public static final String qn_user_auth_last_update = "qn_user_auth_last_update";
    public static final boolean sDisableCommonHooks = LicenseStatus.isBlacklisted();

    public static void setEulaStatus(int status) {
        ConfigManager.getDefaultConfig().putInt(qn_eula_status, status);
        try {
            ConfigManager.getDefaultConfig().save();
        } catch (IOException e) {
            log(e);
            Toasts.error(HostInfo.getHostInfo().getApplication(), e.toString());
        }
    }

    public static boolean hasEulaUpdated() {
        return false;
    }

    public static boolean hasUserAcceptEula() {
        return true;
    }

    public static boolean isInsider() {
        return true;
    }

    public static boolean isBlacklisted() {
        return false;
    }

    public static boolean isWhitelisted() {
        return true;
    }

    public static boolean isAsserted() {
        return true;
    }

}
