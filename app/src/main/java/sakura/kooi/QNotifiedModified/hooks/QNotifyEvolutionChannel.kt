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

package sakura.kooi.QNotifiedModified.hooks

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import cc.chenhe.qqnotifyevo.core.NevoNotificationProcessor
import cc.chenhe.qqnotifyevo.utils.getNotificationChannels
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.tlb.增强功能
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.Utils

@FunctionEntry
@UiItem
object QNotifyEvolutionChannel : CommonDelayAbleHookBridge(SyncUtils.PROC_ANY) {
    @SuppressLint("StaticFieldLeak")
    private lateinit var processor: NevoNotificationProcessor

    override fun isValid(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    override fun initOnce(): Boolean {
        return try {
            processor = NevoNotificationProcessor(hostInfo.application)
            XposedBridge.hookAllMethods(
                NotificationManager::class.java,
                "notify",
                object : XC_MethodHook() {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        try {
                            val notification: Notification =
                                param.args[param.args.size - 1] as Notification

                            createNotificationChannels()
                            val decoratedNotification: Notification? = processor.resolveNotification(hostInfo.application, hostInfo.packageName, notification);
                            if (decoratedNotification != null) {
                                param.args[param.args.size - 1] = decoratedNotification
                            }
                        } catch (e: Exception) {
                            Utils.log(e)
                        }
                    }
                })
            true
        } catch (e: Exception) {
            Utils.log(e)
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        val notificationChannels: List<NotificationChannel> = getNotificationChannels()
        val notificationChannelGroup : NotificationChannelGroup = NotificationChannelGroup("qq_evolution", "QQ通知进化")
        val notificationManager: NotificationManager = hostInfo.application.getSystemService(NotificationManager::class.java);
        if (notificationChannels.any {
            notificationChannel -> notificationManager.getNotificationChannel(notificationChannel.id) == null
        }) {
            Log.i("QNotifyEvolutionXp", "Creating channels...");
            notificationManager.createNotificationChannelGroup(notificationChannelGroup)
            notificationManager.createNotificationChannels(notificationChannels);
        }
    }

    override val preference = object : UiSwitchPreferenceItemFactory() {
        override var title: String = "QQ通知进化"
        override var summary: String?
            get() {
                val sb: StringBuilder = StringBuilder()
                sb.append("QQ-Notify-Evolution with Xposed!")
                if (!isValid) {
                    sb.append(" 仅支持Android O及以上")
                }
                return sb.toString()
            }
            set(value) {}
    }

    override val preferenceLocate: Array<String> = 增强功能

}
