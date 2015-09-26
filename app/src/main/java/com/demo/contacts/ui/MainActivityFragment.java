package com.demo.contacts.ui;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    @Bind(R.id.contact_country)
    TextView countryTV;

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

    @OnClick({ R.id.search_btn})
    public void startSearch(){

        String searchStr = searchField.getText().toString();
        if(searchStr != null && searchStr.length()>0) {
            progress.setVisibility(View.VISIBLE);
//            ContactsUtil.getInstance().search(searchStr, this);
            ContactsUtil.getInstance().search(searchStr,this);
        }

    }

    @Override
    public void onFinish(Contact contact,LOADED_PART loaded_part) {
        switch(loaded_part){
            case BASE:
                //name,image,phone
                if(contact.getThumbUri() != null) {
                    Picasso.with(getActivity()).load(contact.getThumbUri()).into(imageView);
                }
                phoneNumberTV.setText(contact.getPhoneNumber());
                displyNameTV.setText(contact.getDisplayName());
                break;
            case DETAILS:
                if(contact != null){
                    cityTV.setText(contact.getCity());
                    streetTV.setText(contact.getStreet());
                    postTV.setText(contact.getPostcode());
                    countryTV.setText(contact.getCountry());

                }
                progress.setVisibility(View.GONE);
                break;
        }

    }
}
