package com.demo.contacts.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.contacts.R;
import com.demo.contacts.domain.Contact;
import com.demo.contacts.util.ContactsUtil;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements ContactsLoaderListener{
    @Bind(R.id.search_field)
     EditText searchField;
    @Bind(R.id.contact_city)
    TextView cityTV;
    @Bind(R.id.contact_display_name)
    TextView displyNameTV;
    @Bind(R.id.contact_image)
    ImageView imageView;
    @Bind(R.id.contact_phone_number)
    TextView phoneNumberTV;
    @Bind(R.id.contact_post)
    TextView postTV;
    @Bind(R.id.contact_street)
    TextView streetTV;
    @Bind(R.id.search_progress)
    ProgressBar progress;
    @Bind(R.id.details)
    RelativeLayout details;
    @Bind(R.id.contact_country)
    TextView countryTV;

    @Bind(R.id.search_btn)
    ImageView searchBtn;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContactsUtil.createInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ContactsUtil.getInstance().destroyLoader();
    }

    @OnClick({ R.id.search_btn})
    public void startSearch(){
        hideKeyboard();
        String searchStr = searchField.getText().toString();
        if(searchStr != null && searchStr.length()>0){
            cleanContactViews();
            searchBtn.setEnabled(false);
            progress.setVisibility(View.VISIBLE);
            ContactsUtil.getInstance().search(searchStr,this);
        }else{
            Toast.makeText(getActivity(),"Entry not valid !",Toast.LENGTH_SHORT);
        }

    }

    /**
     * Contact must not be null and contain phone number
     * @param contact
     * @return true if contact is not null and contain phone number
     */
    private boolean isContactValid(Contact contact){
        return contact != null && contact.getPhoneNumber() != null;
    }
    @Override
    public void onResult(Contact contact, LOADED_PART loaded_part) {
        if(!isContactValid(contact)){
            return;
        }
        switch(loaded_part){
            case BASE:
                //name,image,phone
                details.setVisibility(View.VISIBLE);
                if(contact.getThumbUri() != null) {
                    Picasso.with(getActivity()).load(contact.getThumbUri()).into(imageView);
                }
                phoneNumberTV.setText(contact.getPhoneNumber());
                displyNameTV.setText(contact.getDisplayName());
                break;
            case DETAILS:
                    cityTV.setText(contact.getCity());
                    streetTV.setText(contact.getStreet());
                    postTV.setText(contact.getPostcode());
                    countryTV.setText(contact.getCountry());

                break;
        }
    }
    private void cleanContactViews(){
        phoneNumberTV.setText("");
        displyNameTV.setText("");
        cityTV.setText("");
        streetTV.setText("");
        postTV.setText("");
        countryTV.setText("");
        this.details.setVisibility(View.GONE);
    }

    @Override
    public void onFinish() {
        searchBtn.setEnabled(true);
        progress.setVisibility(View.GONE);

    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchField.getWindowToken(), 0);
        getView().invalidate();
    }
}
