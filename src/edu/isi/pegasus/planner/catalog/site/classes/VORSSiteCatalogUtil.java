/*
 * 
 *   Copyright 2007-2008 University Of Southern California
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 */
package edu.isi.pegasus.planner.catalog.site.classes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.griphyn.cPlanner.classes.Profile;
import org.griphyn.cPlanner.common.LogManager;

import edu.isi.pegasus.planner.catalog.site.classes.GridGateway.SCHEDULER_TYPE;

public class VORSSiteCatalogUtil {
	 private static LogManager mLogger = LogManager.getInstance();	 
	 public static List<VORSVOInfo> get_sites_in_grid(String host, String port, String vo, String grid){
	    	URL vors = null;
	    	URLConnection vc = null;
	    	BufferedReader in = null;
	        String inputLine = null;
	        ArrayList<VORSVOInfo> ret = new ArrayList<VORSVOInfo>();
			try {
				vors = new URL("http://" + host + ":" + port + "/cgi-bin/tindex.cgi?VO=" + vo + "&grid=" + grid);
						//"http://vors.grid.iu.edu/cgi-bin/tindex.cgi?VO=ligo&grid=osg");
				vc = vors.openConnection();		
				in = new BufferedReader(new InputStreamReader(vc.getInputStream()));
	        
				while ((inputLine = in.readLine()) != null){
					inputLine = inputLine.trim();
					//ignore commented or empty lines				
					if(inputLine.startsWith("#") || inputLine.equals("") ){
					    continue;
					}
					//#columns=ID,Name,Gatekeeper,Type,Grid,Status,Last Test Date
					String[] col = inputLine.split(",");
					VORSVOInfo vinfo = new VORSVOInfo();
					vinfo.setID(col[0]);
					vinfo.setName(col[1]);
					vinfo.setGatekeeper(col[2]);
					vinfo.setType(col[3]);
					vinfo.setGrid(col[4]);
					vinfo.setStatus(col[5]);
					vinfo.setLast_Test_Date(col[6]);
					
					ret.add(vinfo);
				    //System.out.println(inputLine);
				}
	        } catch (MalformedURLException e) {
				e.printStackTrace();
			}
	        catch (IOException e) {
				e.printStackTrace();
			}    
	        finally{
		        try {
					in.close();
				} catch (IOException e) {				
					e.printStackTrace();
				}
	        }
	        return ret;
	    }
	    public static VORSSiteInfo get_sites_info(String host, String port, String vo, String grid, String id){
	    	URL vors = null;
	    	URLConnection vc = null;
	    	BufferedReader in = null;
	        String inputLine = null;
	        VORSSiteInfo ret = new VORSSiteInfo();
	        Map values = new HashMap();
			try {
				vors = new URL("http://" + host + ":" + port + "/cgi-bin/tindex.cgi?VO=" + vo + "&grid=" + grid + "&res=" + id);
						//"http://vors.grid.iu.edu/cgi-bin/tindex.cgi?VO=ligo&grid=osg&res=" + id);
				vc = vors.openConnection();		
				in = new BufferedReader(new InputStreamReader(vc.getInputStream()));
	            
				while ((inputLine = in.readLine()) != null){ 
					//ignore commented or empty lines
					inputLine = inputLine.trim();
					if(inputLine.startsWith("#") || inputLine.equals("") ){
					    continue;
					}
					
					String[] col = inputLine.split("=");
					if(col.length > 1){
						values.put(col[0], col[1]);
					}
				}		
				
	        } catch (MalformedURLException e) {
				e.printStackTrace();
			}
	        catch (IOException e) {
				e.printStackTrace();
			}    
	        finally{
		        try {
					in.close();
				} catch (IOException e) {				
					e.printStackTrace();
				}
	        }
	    	ret.setApp_loc((String)values.get("app_loc"));
			ret.setGatekeeper((String)values.get("gatekeeper"));
			ret.setGk_port((String)values.get("gk_port"));		
			ret.setGsiftp_port((String)values.get("gsiftp_port"));
			ret.setData_loc((String)values.get("data_loc"));
			ret.setOsg_grid((String)values.get("osg_grid"));
			ret.setShortname((String)values.get("shortname"));		
			ret.setTmp_loc((String)values.get("tmp_loc"));
			ret.setVdt_version((String)values.get("vdt_version"));
			ret.setSponsor_vo((String)values.get("sponsor_vo"));
			ret.setWntmp_loc((String)values.get("wntmp_loc"));
			ret.setApp_space((String)values.get("app_space"));
			ret.setData_space((String)values.get("data_space"));
			ret.setExec_jm((String)values.get("exec_jm"));
			ret.setGlobus_loc((String)values.get("globus_loc"));
			ret.setGrid_services((String)values.get("grid_services"));
			ret.setTmp_space((String)values.get("tmp_space"));
			ret.setUtil_jm((String)values.get("util_jm"));		
	        return ret;
	    }

	    /*public static String getGatekeeper(VORSSiteInfo sitInfo) {    	
	    	String host = sitInfo.getGatekeeper();
	    	String port = sitInfo.getGk_port();
	    	if(host == null || host.equals("")){
	    		 mLogger.log( "Gatekeeper hostname missing in "+ sitInfo.getVoInfo().getName() +" using gatekeeper entry.",
	                     LogManager.CONFIG_MESSAGE_LEVEL);	    		
	    		host = (sitInfo.getVoInfo().getGatekeeper().split(":"))[0];    		    	
	    	}
	    	if(port == null || port.equals("")){
	    		 mLogger.log( "Gatekeeper hostname missing in " + sitInfo.getVoInfo().getName() + " using default 2119.",
	                     LogManager.CONFIG_MESSAGE_LEVEL);	    		    	
	    		port = "2119";
	    	}
	    	if(port.equals("2119")){
	    		return host;
	    	}
	    	else{
	    		return host + ":" + port;
	    	}        
	    }*/
	    public static String getGsiftp(VORSSiteInfo sitInfo) {
	    	String host = sitInfo.getGatekeeper();
	    	String port = sitInfo.getGsiftp_port();
	    	if(host == null || host.equals("")){
	    		 mLogger.log( "Gridftp hostname missing in "+ sitInfo.getVoInfo().getName() +" using gatekeeper entry.",
	                     LogManager.CONFIG_MESSAGE_LEVEL);	    		
	    		host = (sitInfo.getVoInfo().getGatekeeper().split(":"))[0];
	    	}
	    	if(port == null || port.equals("")){
	    		mLogger.log( "Gridftp hostname missing in " + sitInfo.getVoInfo().getName() + " using default 2811.",
	                     LogManager.CONFIG_MESSAGE_LEVEL);
	    		port = "2811";
	    	}    	
	    	if(port.equals("2811")){
	    		return "gsiftp://" + host;
	    	}
	    	else{
	    		return "gsiftp://" + host + ":" + port;
	    	}        
	    }
	    public static SiteCatalogEntry createSiteCatalogEntry(VORSSiteInfo sitInfo){
	        SiteCatalogEntry entry = new SiteCatalogEntry( sitInfo.getShortname());
	  
	        entry.setHeadNodeFS( createHeadNodeFS(sitInfo) );
	        entry.setWorkerNodeFS( createWorkerNodeFS(sitInfo) );
	        
	        //associate a replica catalog with the site.
	        ReplicaCatalog rc = new ReplicaCatalog( "rls://replica.isi.edu", "RLS" );
	        
	        rc.addAlias( sitInfo.getShortname());
	        rc.addConnection( new Connection("ignore.lrc", "rls://replica.caltech.edu" ));            
	        entry.addReplicaCatalog( rc );
	        
	        //associate some profiles
	        
	        entry.addProfile( new Profile( Profile.ENV, "GLOBUS_LOCATION", ((sitInfo.getGlobus_loc() != null)?sitInfo.getGlobus_loc():"/") ) );
	        entry.addProfile( new Profile( Profile.ENV, "LD_LIBRARY_PATH", ((sitInfo.getGlobus_loc() != null)?sitInfo.getGlobus_loc():"") + "/lib") );
	        entry.addProfile( new Profile( Profile.ENV, "PEGASUS_HOME", ((sitInfo.getOsg_grid() != null)?sitInfo.getOsg_grid():"") +"/pegasus")) ;
	        entry.addProfile( new Profile( Profile.ENV, "app_loc",((sitInfo.getApp_loc() != null)?sitInfo.getApp_loc():"/")) );
	        entry.addProfile( new Profile( Profile.ENV, "data_loc", ((sitInfo.getData_loc() != null)?sitInfo.getData_loc():"/")) );
	        entry.addProfile( new Profile( Profile.ENV, "osg_grid", ((sitInfo.getOsg_grid() != null)?sitInfo.getOsg_grid():"/")) );
	        entry.addProfile( new Profile( Profile.ENV, "tmp_loc", ((sitInfo.getTmp_loc() != null)?sitInfo.getTmp_loc():"/")) );
	        entry.addProfile( new Profile( Profile.ENV, "wntmp_loc", ((sitInfo.getWntmp_loc() != null)?sitInfo.getWntmp_loc():"/")) );
	        entry.addProfile( new Profile( Profile.VDS, "grid", ((sitInfo.getVoInfo().getGrid() != null)?sitInfo.getVoInfo().getGrid():"")) );
	        
	        //associate grid gateway for auxillary and compute jobs
	        GridGateway gw = new GridGateway( GridGateway.TYPE.gt2,
	        									((sitInfo.getExec_jm() != null)?sitInfo.getExec_jm():
	        										(sitInfo.getVoInfo().getGatekeeper().split(":"))[0] + "/jobmanager-fork"),
	        									getSchedulerType(sitInfo.getExec_jm()) );
	        gw.setJobType( GridGateway.JOB_TYPE.auxillary );        
	        
	        entry.addGridGateway( gw );
	        
	        gw = new GridGateway( GridGateway.TYPE.gt2,        									
	        									((sitInfo.getUtil_jm() != null)?sitInfo.getUtil_jm():
	        										(sitInfo.getVoInfo().getGatekeeper().split(":"))[0] + "/jobmanager-fork"),
	        									getSchedulerType(sitInfo.getUtil_jm()) );
	        gw.setJobType( GridGateway.JOB_TYPE.compute );
	        entry.addGridGateway( gw );
	        return entry;
	    }
	    private static SCHEDULER_TYPE getSchedulerType(String url) {
	    	if(url == null){
	    		return GridGateway.SCHEDULER_TYPE.Fork ;
	    	}
	        if( url.endsWith( "condor" ) ){
	            return  GridGateway.SCHEDULER_TYPE.Condor ;
	        }
	        else if( url.endsWith( "fork" ) ){
	            return GridGateway.SCHEDULER_TYPE.Fork ;
	        }
	        else if( url.endsWith( "pbs" ) ){
	            return GridGateway.SCHEDULER_TYPE.PBS ;
	        }
	        else if( url.endsWith( "lsf" ) ){
	            return GridGateway.SCHEDULER_TYPE.LSF ;
	        }  
	        //if nothing is there than return fork
	        return GridGateway.SCHEDULER_TYPE.Fork ;
		}
		/**
	     * Creates an object describing the head node filesystem.
	     * 
	     * @return the HeadNodeFS
	     */
	    /*head node :
	    	shared:
	    	    scratch data_loc
	    	    storage data_loc
	    	local:   
	    	    scratch tmp_loc
	    	    storage tmp_loc
	    */
	    public static HeadNodeFS createHeadNodeFS(VORSSiteInfo sitInfo){
	        // describe the head node filesystem
	        HeadNodeFS hfs = new HeadNodeFS();
	            
	        //head node scratch description start
	        HeadNodeScratch hscratch = new HeadNodeScratch();            
	        
	        //head node local scratch description
	        LocalDirectory hscratchLocal = new LocalDirectory();
	        FileServer f = new FileServer( "gsiftp", getGsiftp(sitInfo), (sitInfo.getTmp_loc() != null)?sitInfo.getTmp_loc():"/" );
	        hscratchLocal.addFileServer( f );      
	        hscratchLocal.setInternalMountPoint( new InternalMountPoint( "/local", "50G", "100G") );
	        
	        //head node shared scratch description
	        SharedDirectory hscratchShared = new SharedDirectory();            
	        f = new FileServer( "gsiftp", getGsiftp(sitInfo), (sitInfo.getData_loc() != null)?sitInfo.getData_loc():"/" );
	        f.addProfile( new Profile( Profile.VDS, "transfer.arguments", "-s -a"));                    
	        hscratchShared.addFileServer( f );
	        hscratchShared.setInternalMountPoint( new InternalMountPoint( "/shared-scratch", "50G", "100G") );
	        hscratch.setLocalDirectory( hscratchLocal );
	        hscratch.setSharedDirectory( hscratchShared );
	        //head node scratch description ends
	            
	        //head node storage description start
	        HeadNodeStorage hstorage = new HeadNodeStorage();
	            
	        //head node local storage description            
	        LocalDirectory hstorageLocal = new LocalDirectory();
	        f = new FileServer( "gsiftp", getGsiftp(sitInfo), (sitInfo.getTmp_loc() != null)?sitInfo.getTmp_loc():"/"  );
	        hstorageLocal.addFileServer( f );
	        hstorageLocal.setInternalMountPoint( new InternalMountPoint( "/local-storage", "30G", "100G") );
	        //head node shared storage description
	        SharedDirectory hstorageShared = new SharedDirectory();            
	        f = new FileServer( "gsiftp", getGsiftp(sitInfo), (sitInfo.getData_loc() != null)?sitInfo.getData_loc():"/"  );
	        f.addProfile( new Profile( Profile.VDS, "transfer.arguments", "-s -a"));                    
	        hstorageShared.addFileServer( f );
	        hstorageShared.setInternalMountPoint( new InternalMountPoint( "/shared-storage", "50G", "100G") );
	        hstorage.setLocalDirectory( hstorageLocal );
	        hstorage.setSharedDirectory( hstorageShared );            
	        //head node storage description ends
	            
	        hfs.setScratch( hscratch );
	        hfs.setStorage( hstorage );
	            
	        return hfs;
	    }
	    
	    /**
	     * Creates an object describing the worker node filesystem.
	     * 
	     * @return the HeadNodeFS
	     */
	    /*worker node:
	    	shared:
	    	    scratch data
	    	    storage data
	    	local:   
	    	    scratch wntmp
	    	    storage wntmp
	    	    */
	    public static WorkerNodeFS createWorkerNodeFS(VORSSiteInfo sitInfo){
	        // describe the head node filesystem
	        WorkerNodeFS wfs = new WorkerNodeFS();
	            
	        //worker node scratch description start
	        WorkerNodeScratch wscratch = new WorkerNodeScratch();            
	        //worker node local scratch description
	        LocalDirectory wscratchLocal = new LocalDirectory();
	        FileServer f = new FileServer( "file", "file:///", (sitInfo.getWntmp_loc() != null)?sitInfo.getWntmp_loc():"/" );
	        wscratchLocal.addFileServer( f );
	        wscratchLocal.setInternalMountPoint( new InternalMountPoint( "/tmp", "50G", "100G") );
	        //worker node shared scratch description
	        SharedDirectory wscratchShared = new SharedDirectory();            
	        f = new FileServer( "file", "file:///", (sitInfo.getData_loc() != null)?sitInfo.getData_loc():"/" );
	        wscratchShared.setInternalMountPoint( new InternalMountPoint( "/shared-scratch", "50G", "100G") );
	        wscratch.setLocalDirectory( wscratchLocal );
	        wscratch.setSharedDirectory( wscratchShared );
	        //head node scratch description ends                    
	        
	        //worker node storage description start
	        WorkerNodeStorage wstorage = new WorkerNodeStorage();            
	        //worker node local scratch description
	        LocalDirectory wstorageLocal = new LocalDirectory();
	        f = new FileServer( "file", "file:///", (sitInfo.getWntmp_loc() != null)?sitInfo.getWntmp_loc():"/" );
	        wstorageLocal.addFileServer( f );
	        wstorageLocal.setInternalMountPoint( new InternalMountPoint( "/tmp", "50G", "100G") );
	        
	        //worker node shared scratch description
	        SharedDirectory wstorageShared = new SharedDirectory();            
	        f = new FileServer( "file", "file:///", (sitInfo.getData_loc() != null)?sitInfo.getData_loc():"/" );
	        wstorageShared.setInternalMountPoint( new InternalMountPoint( "/shared-storage", "50G", "100G") );
	        wstorage.setLocalDirectory( wstorageLocal );
	        wstorage.setSharedDirectory( wstorageShared );
	        //worker node scratch description ends
	        //worker node storage description ends
	            
	        wfs.setScratch( wscratch );
	        wfs.setStorage( wstorage );
	            
	        return wfs;
	    }	 	   
}
