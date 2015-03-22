package com.example.moviebook;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.FragmentTransaction;


public class MainActivity extends Activity implements 
	MovieListFragment.MovieListFragmentListener,
	DetailsFragment.DetailsFragmentListener, 
	AddEditFragment.AddEditFragmentListener
{

	// keys for storing row ID in Bundle passed to a fragment
	   public static final String ROW_ID = "row_id"; 
	   
	   MovieListFragment movieListFragment; // displays contact list
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // return if Activity is being restored, no need to recreate GUI
        if (savedInstanceState != null) 
           return;

        // check whether layout contains fragmentContainer (phone layout);
        // ContactListFragment is always displayed
        if (findViewById(R.id.fragmentContainer) != null) 
        {
           // create ContactListFragment
           movieListFragment = new MovieListFragment();
           
           // add the fragment to the FrameLayout
           FragmentTransaction transaction = 
              getFragmentManager().beginTransaction();
           transaction.add(R.id.fragmentContainer, movieListFragment);
           transaction.commit(); // causes ContactListFragment to display
        }
    }
    
 // called when MainActivity resumes
    @Override
    protected void onResume()
    {
       super.onResume();
       
       // if contactListFragment is null, activity running on tablet, 
       // so get reference from FragmentManager
       if (movieListFragment == null)
       {
          movieListFragment = 
             (MovieListFragment) getFragmentManager().findFragmentById(
                R.id.contactListFragment);      
       }
    }
    
 // display DetailsFragment for selected contact
    @Override
    public void onMovieSelected(long rowID)
    {
       if (findViewById(R.id.fragmentContainer) != null) // phone
          displayMovie(rowID, R.id.fragmentContainer);
       else // tablet
       {
          getFragmentManager().popBackStack(); // removes top of back stack
          displayMovie(rowID, R.id.rightPaneContainer);
       }
    }
    
 // display a contact
    private void displayMovie(long rowID, int viewID)
    {
       DetailsFragment detailsFragment = new DetailsFragment();
       
       // specify rowID as an argument to the DetailsFragment
       Bundle arguments = new Bundle();
       arguments.putLong(ROW_ID, rowID);
       detailsFragment.setArguments(arguments);
       
       // use a FragmentTransaction to display the DetailsFragment
       FragmentTransaction transaction = 
          getFragmentManager().beginTransaction();
       transaction.replace(viewID, detailsFragment);
       transaction.addToBackStack(null);
       transaction.commit(); // causes DetailsFragment to display
    }
    
 // display the AddEditFragment to add a new contact
    @Override
    public void onAddMovie()
    {
       if (findViewById(R.id.fragmentContainer) != null)
          displayAddEditFragment(R.id.fragmentContainer, null); 
       else
          displayAddEditFragment(R.id.rightPaneContainer, null);
    }

 // display fragment for adding a new or editing an existing contact
    private void displayAddEditFragment(int viewID, Bundle arguments)
    {
       AddEditFragment addEditFragment = new AddEditFragment();
       
       if (arguments != null) // editing existing contact
          addEditFragment.setArguments(arguments);
       
       // use a FragmentTransaction to display the AddEditFragment
       FragmentTransaction transaction = 
          getFragmentManager().beginTransaction();
       transaction.replace(viewID, addEditFragment);
       transaction.addToBackStack(null);
       transaction.commit(); // causes AddEditFragment to display
    }
    
 // return to contact list when displayed contact deleted
    @Override
    public void onMovieDeleted()
    {
       getFragmentManager().popBackStack(); // removes top of back stack
       
       if (findViewById(R.id.fragmentContainer) == null) // tablet
          movieListFragment.updateContactList();
    }
    
 // display the AddEditFragment to edit an existing contact
    @Override
    public void onEditMovie(Bundle arguments)
    {
       if (findViewById(R.id.fragmentContainer) != null) // phone
          displayAddEditFragment(R.id.fragmentContainer, arguments); 
       else // tablet
          displayAddEditFragment(R.id.rightPaneContainer, arguments);
    }
    
 // update GUI after new contact or updated contact saved
    @Override
    public void onAddEditCompleted(long rowID)
    {
       getFragmentManager().popBackStack(); // removes top of back stack

       if (findViewById(R.id.fragmentContainer) == null) // tablet
       {
          getFragmentManager().popBackStack(); // removes top of back stack
          movieListFragment.updateContactList(); // refresh contacts

          // on tablet, display contact that was just added or edited
          displayMovie(rowID, R.id.rightPaneContainer); 
       }
    }  

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
