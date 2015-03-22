package com.example.moviebook;

import android.app.ListFragment;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MovieListFragment extends ListFragment {

	// callback methods implemented by MainActivity  
	   public interface MovieListFragmentListener
	   {
	      // called when user selects a contact
	      public void onMovieSelected(long rowID);

	      // called when user decides to add a contact
	      public void onAddMovie();
	   }
	   
	   private MovieListFragmentListener listener; 
	   
	   private ListView contactListView; // the ListActivity's ListView
	   private CursorAdapter contactAdapter; // adapter for ListView
	   
	// set ContactListFragmentListener when fragment attached   
	   @Override
	   public void onAttach(Activity activity)
	   {
	      super.onAttach(activity);
	      listener = (MovieListFragmentListener) activity;
	   }
	   
	   // remove ContactListFragmentListener when Fragment detached
	   @Override
	   public void onDetach()
	   {
	      super.onDetach();
	      listener = null;
	   }
	   
	// called after View is created
	   @Override
	   public void onViewCreated(View view, Bundle savedInstanceState)
	   {
	      super.onViewCreated(view, savedInstanceState);
	      setRetainInstance(true); // save fragment across config changes
	      setHasOptionsMenu(true); // this fragment has menu items to display

	      // set text to display when there are no contacts
	      setEmptyText(getResources().getString(R.string.no_movies));

	      // get ListView reference and configure ListView
	      contactListView = getListView(); 
	      contactListView.setOnItemClickListener(viewMovieListener);      
	      contactListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	      
	      // map each contact's name to a TextView in the ListView layout
	      String[] from = new String[] { "title" };
	      int[] to = new int[] { android.R.id.text1 };
	      contactAdapter = new SimpleCursorAdapter(getActivity(), 
	         android.R.layout.simple_list_item_1, null, from, to, 0);
	      setListAdapter(contactAdapter); // set adapter that supplies data
	   }
	
	// responds to the user touching a contact's name in the ListView
	   OnItemClickListener viewMovieListener = new OnItemClickListener() 
	   {
	      @Override
	      public void onItemClick(AdapterView<?> parent, View view, 
	         int position, long id) 
	      {
	         listener.onMovieSelected(id); // pass selection to MainActivity
	      } 
	   }; // end viewContactListener
	   
	// when fragment resumes, use a GetContactsTask to load contacts 
	   @Override
	   public void onResume() 
	   {
	      super.onResume(); 
	      new GetMoviesTask().execute((Object[]) null);
	   }
	   
	// performs database query outside GUI thread
	   private class GetMoviesTask extends AsyncTask<Object, Object, Cursor> 
	   {
	      DatabaseConnector databaseConnector = 
	         new DatabaseConnector(getActivity());

	      // open database and return Cursor for all contacts
	      @Override
	      protected Cursor doInBackground(Object... params)
	      {
	         databaseConnector.open();
	         return databaseConnector.getAllMovies(); 
	      } 

	      // use the Cursor returned from the doInBackground method
	      @Override
	      protected void onPostExecute(Cursor result)
	      {
	         contactAdapter.changeCursor(result); // set the adapter's Cursor
	         databaseConnector.close();
	      } 
	   } // end class GetContactsTask
	   
	// when fragment stops, close Cursor and remove from contactAdapter 
	   @Override
	   public void onStop() 
	   {
	      Cursor cursor = contactAdapter.getCursor(); // get current Cursor
	      contactAdapter.changeCursor(null); // adapter now has no Cursor
	      
	      if (cursor != null) 
	         cursor.close(); // release the Cursor's resources
	      
	      super.onStop();
	   } 
	   
	// display this fragment's menu items
	   @Override
	   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	   {
	      super.onCreateOptionsMenu(menu, inflater);
	      inflater.inflate(R.menu.fragment_movie_list_menu, menu);
	   }
	   
	// handle choice from options menu
	   @Override
	   public boolean onOptionsItemSelected(MenuItem item) 
	   {
	      switch (item.getItemId())
	      {
	         case R.id.action_add:
	            listener.onAddMovie();
	            return true;
	      }
	      
	      return super.onOptionsItemSelected(item); // call super's method
	   }
	   
	   // update data set
	   public void updateContactList()
	   {
	      new GetMoviesTask().execute((Object[]) null);
	   }
}
