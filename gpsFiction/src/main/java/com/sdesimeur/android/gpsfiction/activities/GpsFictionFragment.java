package com.sdesimeur.android.gpsfiction.activities;

import android.support.v4.app.Fragment;

@SuppressWarnings("unused")
public class GpsFictionFragment extends Fragment {
/*	private static final String TAGFONT = "FONT";
    public static final String TAGNAME = "retainFragment";
	private GpsFictionActivity gpsFictionActivity = null;
	private MyLocationListener myLocationListener = null;
	private GpsFictionData gpsFictionData = null;
	
	public GpsFictionFragment() {
		super();
		}
	public void init(GpsFictionActivity gpsFictionActivity) {
		this.gpsFictionActivity = gpsFictionActivity;
		if ( this.myLocationListener == null ) this.myLocationListener = new MyLocationListener();
	    this.myLocationListener.init(this);
	    if ( this.gpsFictionData == null ) this.gpsFictionData = new GpsFictionData();
		this.gpsFictionData.init(this);
	}

	public MyLocationListener getMyLocationListener() {
		// TODO Auto-generated method stub
		return this.myLocationListener;
	}

	public GpsFictionData getGpsFictionData() {
		return gpsFictionData;
	}
	public GpsFictionActivity getGpsFictionActivity() {
		return this.gpsFictionActivity;
	}
	public Typeface getFontFromRes(int resource) { 
	    Typeface tf = null;
	    InputStream is = null;
	    try {
	        is = this.getResources().openRawResource(resource);
	    }
	    catch(NotFoundException e) {
	        Log.e(TAGFONT, "Could not find font in resources!");
	    }

	    String outPath = this.getActivity().getCacheDir() + "/tmp" + System.currentTimeMillis() + ".raw";

	    try
	    {
	        byte[] buffer = new byte[is.available()];
	        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPath));

	        int l = 0;
	        while((l = is.read(buffer)) > 0)
	            bos.write(buffer, 0, l);

	        bos.close();

	        tf = Typeface.createFromFile(outPath);

	        // clean up
	        new File(outPath).delete();
	    }
	    catch (IOException e)
	    {
	        Log.e(TAGFONT, "Error reading in font!");
	        return null;
	    }

	    Log.d(TAGFONT, "Successfully loaded font.");

	    return tf;      
	}

	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		//this.gpsFictionActivity = (GpsFictionActivity) activity;
	}
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    // Retain this fragment across configuration changes.
	  }
	  @Override
	  public void onDetach() {
		  super.onDetach();
		  this.getMyLocationListener().removeGpsFictionUpdates();
	  }
	  

	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
		super.onCreateView(layoutInflater, viewGroup, bundle );
		return null;
	}
	@Override
	public void onActivityCreated(Bundle bundle) {
		super.onActivityCreated(bundle);
	}
	@Override
	public void onViewStateRestored(Bundle bundle) {
		super.onViewStateRestored( bundle);
	}
	@Override
	public void onStart() {
		super.onStart();
		this.setRetainInstance(true);
	}
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
	}
	@Override
	public void onStop() {
		super.onStop();
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
*/
}
