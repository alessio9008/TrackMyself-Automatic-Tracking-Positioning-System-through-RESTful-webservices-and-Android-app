package it.unict.dieei.semm.trackmyself.dialogs;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.unict.dieei.semm.trackmyself.FavoriteRoutesActivity;
import it.unict.dieei.semm.trackmyself.MapActivity;
import it.unict.dieei.semm.trackmyself.R;
import it.unict.dieei.semm.trackmyself.R.string;
import it.unict.dieei.semm.trackmyself.util.Favorite;
import it.unict.dieei.semm.trackmyself.util.Utils;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

public class FavoriteItemOptionsDialogFragment extends DialogFragment {

	private Favorite favorite;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		favorite = (Favorite)this.getArguments().get("favoriteKey");
		
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(favorite.getFavoriteName())
	           .setItems(R.array.favorite_context_menu_items, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   FavoriteRoutesActivity act = (FavoriteRoutesActivity)getActivity();
	            	   switch(which){
	            	   		case 0:
	            	   			act.showFavoriteRouteOnMap(favorite);
	            	   			break;
	            	   		case 1:
	            	   			act.editRoute(favorite);
	            	   			break;
	            	   		case 2:
	            	   			act.deleteRoute(favorite);
	            	   			break;
	            	   }
	           }
	    });
	    return builder.create();
	}

	public Favorite getFavorite() {
		return favorite;
	}
}