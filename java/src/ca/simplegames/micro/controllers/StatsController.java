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
import ca.simplegames.micro.Globals;
import ca.simplegames.micro.MicroContext;
import com.sun.management.OperatingSystemMXBean;
import org.jrack.Rack;
import org.jrack.utils.Mime;
import org.json.JSONException;
import org.json.JSONObject;

import javax.management.MBeanServerConnection;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    public static final String JSON_TYPE = ".json";
    public static final String POWERED_BY_MICRO = "Micro " + Globals.VERSION;

    public void execute(MicroContext context, Map configuration) throws ControllerException {
        Map<String, Object> systemInfo = new HashMap<String, Object>();
        Map<String, Object> osMap = new HashMap<String, Object>();
        MBeanServerConnection mbeanServer = ManagementFactory.getPlatformMBeanServer();

        OperatingSystemMXBean sunOperatingSystemMXBean = null;
        try {
            sunOperatingSystemMXBean = ManagementFactory.newPlatformMXBeanProxy(
                    mbeanServer, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
        } catch (IOException e) {
            throw new ControllerException(e);
        }

        Runtime rt = Runtime.getRuntime();
        long totalMemory = rt.totalMemory() / MEGA_BYTE;
        long freeMemory = rt.freeMemory() / MEGA_BYTE;
        long usedMemory = totalMemory - freeMemory;

        final long p100 = (int) Math.round(((double) freeMemory / (double) totalMemory) * 100);

        Map<String, Long> memInfo = new HashMap<String, Long>();

        memInfo.put("total", totalMemory);
        memInfo.put("used", usedMemory);
        memInfo.put("free", freeMemory);
        memInfo.put("percent_free", p100);

        systemInfo.put("memory", memInfo);
        systemInfo.put("powered_by", POWERED_BY_MICRO);

        //cpu usage in milli secs
        long currentCpuUsage = sunOperatingSystemMXBean.getProcessCpuTime() / 1000000;
        osMap.put("cpu_usage", currentCpuUsage);
        osMap.put("available_processors", sunOperatingSystemMXBean.getAvailableProcessors());
        osMap.put("system_load_average", sunOperatingSystemMXBean.getSystemLoadAverage());
        osMap.put("committed_virtual_memory_size", sunOperatingSystemMXBean.getCommittedVirtualMemorySize());
        osMap.put("free_physical_memory_size", sunOperatingSystemMXBean.getFreePhysicalMemorySize());
        osMap.put("total_physical_memory_size", sunOperatingSystemMXBean.getTotalPhysicalMemorySize());
        osMap.put("free_swap_space_size", sunOperatingSystemMXBean.getFreeSwapSpaceSize());
        osMap.put("total_swap_space_size", sunOperatingSystemMXBean.getTotalSwapSpaceSize());


        systemInfo.put("os", osMap);

        List<GarbageCollectorMXBean> gc = ManagementFactory.getGarbageCollectorMXBeans();
        List<Map> gcInfo = new ArrayList<Map>();

        for (GarbageCollectorMXBean aGc : gc) {
            Map<String, Object> gcMap = new HashMap<String, Object>();
            gcMap.put("name", aGc.getName());
            gcMap.put("collection_count", aGc.getCollectionCount());
            gcMap.put("collection_time", aGc.getCollectionTime());

            gcInfo.add(gcMap);
        }

        systemInfo.put("gc", gcInfo);

        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        Map<String, Object> threadInfoMap = new HashMap<String, Object>(); // more to come ;)
        threadInfoMap.put("peak_thread_count", threadMXBean.getPeakThreadCount());
        threadInfoMap.put("thread_count", threadMXBean.getThreadCount());
        threadInfoMap.put("total_started_thread_count", threadMXBean.getTotalStartedThreadCount());

        long[] deadlockedThreads = threadMXBean.findMonitorDeadlockedThreads();
        threadInfoMap.put("dead_locked_thread_count", deadlockedThreads != null ? deadlockedThreads.length : 0);
        systemInfo.put("thread_info", threadInfoMap);


        JSONObject sysinfoJson = new JSONObject(Collections.singletonMap("system_info", systemInfo));

        String sysinfoString = null;
        try {
            sysinfoString = context.getRequest().getParameter("pretty") != null ?
                    sysinfoJson.toString(2) : sysinfoJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            throw new ControllerException(e);
        }

        context.getRackResponse()
                .withContentType(Mime.mimeType(JSON_TYPE))
                .withBody(sysinfoString)
                .withContentLength(sysinfoString.length())
                .with(Rack.MESSAGE_STATUS, HttpServletResponse.SC_OK);

        context.halt();
    }
}
