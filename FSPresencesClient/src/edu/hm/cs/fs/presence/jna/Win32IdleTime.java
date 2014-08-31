package edu.hm.cs.fs.presence.jna;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;

public class Win32IdleTime {

    /**
     * Get the amount of milliseconds that have elapsed since the last input event
     * (mouse or keyboard)
     * @return idle time in milliseconds
     */
    public static int getIdleTimeMillisWin32() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
    }
}
