/*
 * Copyright (c)2012. Florin T.PATRASCU
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ca.simplegames.micro.controllers;

import ca.simplegames.micro.Controller;
import ca.simplegames.micro.MicroContext;
import com.sun.management.OperatingSystemMXBean;
import org.jrack.Rack;
import org.jrack.utils.Mime;
import org.json.JSONObject;

import javax.management.MBeanServerConnection;
import javax.servlet.http.HttpServletResponse;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;

/**
 * Provides a simple system info
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: 2012-12-21 5:33 PM)
 */
public class StatsController implements Controller {
    private static final long MEGA_BYTE = 1048576;

    public void execute(MicroContext context, Map configuration) throws Exception {
        Map<String, Object> systemInfo = new HashMap<String, Object>();
        Map<String, Object> osMap = new HashMap<String, Object>();
        MBeanServerConnection mbeanServer = ManagementFactory.getPlatformMBeanServer();

        OperatingSystemMXBean sunOperatingSystemMXBean = ManagementFactory.newPlatformMXBeanProxy(mbeanServer,
                ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, com.sun.management.OperatingSystemMXBean.class);

        Runtime rt = Runtime.getRuntime();
        long totalMemory = rt.totalMemory() / MEGA_BYTE;
        long freeMemory = rt.freeMemory() / MEGA_BYTE;
        long usedMemory = totalMemory - freeMemory;

        final long p100 = (int) Math.round(((double) freeMemory / (double) totalMemory) * 100);

        Map<String, Long> memInfo = new HashMap<String, Long>();

        memInfo.put("total", totalMemory);
        memInfo.put("free", freeMemory);
        memInfo.put("used", usedMemory);
        memInfo.put("percentFree", p100);

        systemInfo.put("memory", memInfo);

        //cpu usage in milli secs
        long currentCpuUsage = sunOperatingSystemMXBean.getProcessCpuTime() / 1000000;
        osMap.put("CPU_Usage", currentCpuUsage);
        osMap.put("AvailableProcessors", sunOperatingSystemMXBean.getAvailableProcessors());
        osMap.put("SystemLoadAverage", sunOperatingSystemMXBean.getSystemLoadAverage());
        osMap.put("CommittedVirtualMemorySize", sunOperatingSystemMXBean.getCommittedVirtualMemorySize());
        osMap.put("FreePhysicalMemorySize", sunOperatingSystemMXBean.getFreePhysicalMemorySize());
        osMap.put("TotalPhysicalMemorySize", sunOperatingSystemMXBean.getTotalPhysicalMemorySize());
        osMap.put("FreeSwapSpaceSize", sunOperatingSystemMXBean.getFreeSwapSpaceSize());
        osMap.put("TotalSwapSpaceSize", sunOperatingSystemMXBean.getTotalSwapSpaceSize());


        systemInfo.put("OS", osMap);

        List<GarbageCollectorMXBean> gc = ManagementFactory.getGarbageCollectorMXBeans();
        List<Map> gcInfo = new ArrayList<Map>();

        for (GarbageCollectorMXBean aGc : gc) {

            Map<String, Object> gcMap = new HashMap<String, Object>();
            gcMap.put("Name", aGc.getName());
            gcMap.put("CollectionCount", aGc.getCollectionCount());
            gcMap.put("CollectionTime", aGc.getCollectionTime());

            gcInfo.add(gcMap);
        }

        systemInfo.put("GC", gcInfo);

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threadInfoMap = new HashMap<String, Object>(); // more to come ;)
        threadInfoMap.put("PeakThreadCount", threadMXBean.getPeakThreadCount());
        threadInfoMap.put("ThreadCount", threadMXBean.getThreadCount());
        threadInfoMap.put("TotalStartedThreadCount", threadMXBean.getTotalStartedThreadCount());
        long[] deadlockedThreads = threadMXBean.findMonitorDeadlockedThreads();
        threadInfoMap.put("DeadlockedThreadsCount", deadlockedThreads != null ? deadlockedThreads.length : 0);
        systemInfo.put("ThreadInfo", threadInfoMap);


        JSONObject sysinfoJson = new JSONObject(Collections.singletonMap("systemInfo", systemInfo));

        String sysinfoString = context.getRequest().getParameter("pretty") != null ?
                sysinfoJson.toString(2) : sysinfoJson.toString();

        context.getRackResponse()
                .withHeader("Content-Type", (Mime.mimeType(".json")))
                .withContentLength(sysinfoString.length())
                .withBody(sysinfoString)
                .with(Rack.MESSAGE_STATUS, HttpServletResponse.SC_OK);
        context.halt();
    }
}
