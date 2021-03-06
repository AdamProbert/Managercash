package com.example.managercash_v2.fragments.addExpense;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.example.managercash_v2.BaseActivity;
import com.example.managercash_v2.MainActivity;
import com.example.managercash_v2.database.CategoriesExpense;
import com.example.managercash_v2.database.DatabaseHandler;
import com.example.managercash_v2.database.Expense;

//Used for the text overlay for the grid view on addExpense 

public final class AddExpenseTextAdapter extends BaseAdapter {

	private List<CategoriesExpense> categoriesExpenseList;
	private Context context;
	private DatabaseHandler dh;

	public AddExpenseTextAdapter(Context context) {

		this.context = context;
		dh = new DatabaseHandler(context);
		categoriesExpenseList = dh.getAllExpenseCategories();

	}

	public List<CategoriesExpense> getCategoriesExpenseList() {
		return categoriesExpenseList;
	}

	public void updateTempValue() {
		categoriesExpenseList = null;
		categoriesExpenseList = dh.getAllExpenseCategories();
	}

	public void incrementTemp(int position) {
		CategoriesExpense cE = categoriesExpenseList.get(position);
		cE.add_to_temp_amount(cE.get_increment());
	}

	public void saveTally() {
		if (checkExpenses()) {
			// Gets todays date in string format
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date today = Calendar.getInstance().getTime();
			String date = df.format(today);

			// Loop through categoriesEpenseList, creating expenses. Then resets
			// the lists temporary amount to 0.
			
			int walletId = BaseActivity.getWalletId(); //gets wallet id from base activity
			
			
			for (int i = 0; i < getCount(); i++) {
				if (categoriesExpenseList.get(i).get_temp_amount() > 0) {
					Expense expense = new Expense(walletId, i + 1, date, categoriesExpenseList.get(i).get_temp_amount()); // Using

					dh.createExpense(expense);
				}
			}
			
			BaseActivity.getInstance().refreshContent();

		}

	}

	public void saveTemps() {
		for (CategoriesExpense ce : categoriesExpenseList) {
			dh.updateTempValue(ce);
		}
	}

	public void resetTemps() {
		for (CategoriesExpense ce : categoriesExpenseList) {
			if (ce.get_temp_amount() > 0) {
				ce.set_temp_amount(0);
				dh.updateTempValue(ce);
			}
		}
	}

	public boolean checkExpenses() {
		int total = 0;
		for (int i = 0; i < getCount(); i++) {
			total += categoriesExpenseList.get(i).get_temp_amount();
		}
		if (total > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void setTempAmount(int position, int amount) {
		categoriesExpenseList.get(position).set_temp_amount(amount);

	}

	@Override
	public int getCount() {
		return categoriesExpenseList.size();
	}

	@Override
	public CategoriesExpense getItem(int position) {
		return categoriesExpenseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// This may need to be position +1
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup vg) {

		TextView textView;
		if (convertView == null) {

			Resources r = Resources.getSystem();
			float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
			textView = new TextView(context);
			// Puts text into a grid view that overlays the imageview.
			// (ImageAdapter.java)
			textView.setLayoutParams(new GridView.LayoutParams((int) px, (int) px));
			textView.setPadding(14, 4, 0, 0);
			textView.setTextColor(Color.WHITE);
		} else {
			textView = (TextView) convertView;
		}

		textView.setText(Integer.toString(categoriesExpenseList.get(position).get_temp_amount()));

		return textView;
	}
}