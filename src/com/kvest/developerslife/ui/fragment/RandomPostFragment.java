package com.kvest.developerslife.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.kvest.developerslife.R;
import com.kvest.developerslife.contentprovider.DevlifeProviderMetadata;
import com.kvest.developerslife.network.VolleyHelper;
import com.kvest.developerslife.network.request.GetRandomPostRequest;
import com.kvest.developerslife.network.response.GetRandomPostResponse;
import com.kvest.developerslife.ui.activity.DevlifeBaseActivity;

/**
 * Created with IntelliJ IDEA.
 * User: roman
 * Date: 3/19/14
 * Time: 7:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomPostFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.random_post_fragment, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //clear db
        getActivity().getContentResolver().delete(DevlifeProviderMetadata.RANDOM_POSTS_ITEMS_URI, null, null);

        //load first post
        loadRandomPost();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        VolleyHelper.getInstance().cancelAll(this);
    }

    private void showPost(long postId) {
        DevlifeBaseActivity activity = (DevlifeBaseActivity)getActivity();
        if (activity != null) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            try {
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                transaction.replace(R.id.random_post_container, PostDetailsFragment.newInstance(postId));
            } finally {
                transaction.commit();
            }
        }
    }

    public void refresh() {
        //clear db
        getActivity().getContentResolver().delete(DevlifeProviderMetadata.RANDOM_POSTS_ITEMS_URI, null, null);

        loadRandomPost();
    }

    private void loadRandomPost() {
        GetRandomPostRequest request = new GetRandomPostRequest(new Response.Listener<GetRandomPostResponse>() {
            @Override
            public void onResponse(GetRandomPostResponse response) {
                if (!response.isErrorOccur()) {
                    showPost(response.id);
                } else {
                    showLoadingErrorToast();
                }
            }
        },
        new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showLoadingErrorToast();
            }
        });
        request.setTag(this);
        VolleyHelper.getInstance().addRequest(request);
    }

    private void showLoadingErrorToast() {
        Activity activity = getActivity();
        if (activity != null) {
            Toast.makeText(activity, getText(R.string.error_loading_posts), Toast.LENGTH_LONG).show();
        }
    }
}
