package dev.wolveringer.JUMetrics.data.resolver;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import dev.wolveringer.JUMetrics.data.DataResolver;

public class LocalIpResolver implements DataResolver<String> {
	public String resolveData() throws Exception {
		InetAddress local = getLocalAddress();
		if(local == null)
			local = InetAddress.getLocalHost();
		return local.getHostAddress();
	}
	
	private static InetAddress getLocalAddress() throws Exception{
        Enumeration<NetworkInterface> b = NetworkInterface.getNetworkInterfaces();
        while( b.hasMoreElements()){
            for ( InterfaceAddress f : b.nextElement().getInterfaceAddresses())
                if ( f.getAddress().isSiteLocalAddress())
                    return f.getAddress();
        }
        return null;
    }
}
