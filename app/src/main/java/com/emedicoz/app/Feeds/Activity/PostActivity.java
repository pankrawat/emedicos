package com.emedicoz.app.Feeds.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.emedicoz.app.Common.BaseABNoNavActivity;
import com.emedicoz.app.Feeds.Fragment.CommonFragment;
import com.emedicoz.app.Feeds.Fragment.NewPostFragment;
import com.emedicoz.app.Feeds.Fragment.RegistrationFragment;
import com.emedicoz.app.Feeds.Fragment.TagSelectionFragment;
import com.emedicoz.app.Feeds.Fragment.YouTubeFragment;
import com.emedicoz.app.Model.Tags;
import com.emedicoz.app.R;
import com.emedicoz.app.Response.PostResponse;
import com.emedicoz.app.Response.Registration.SpecializationResponse;
import com.emedicoz.app.Response.Registration.StreamResponse;
import com.emedicoz.app.Response.Registration.SubStreamResponse;
import com.emedicoz.app.Utils.Const;

import java.util.ArrayList;

public class PostActivity extends BaseABNoNavActivity implements TagSelectionFragment.ITagSelectionListener {

    String frag_type, postid, RegType,youtubeid;
    PostResponse post;

    public boolean isimageadded = false;
    public boolean isvideoadded = false;
    public boolean isyoutubevideoattached = false;
    public boolean isattachmentadded = false;

    public String deleted_meta_ids; // this is for all the media which has been removed in the edit post


    public ArrayList<StreamResponse> streamResponseList;
    public ArrayList<SubStreamResponse> substreamResponseList;
    public ArrayList<SpecializationResponse> specializationResponseList;

    @Override
    protected void initViews() {
        if (getIntent().getExtras() != null) {
            post = null;
            frag_type = getIntent().getExtras().getString(Const.FRAG_TYPE);
            postid = getIntent().getExtras().getString(Const.POST_ID);
            post = (PostResponse) getIntent().getExtras().getSerializable(Const.POST);
            RegType = getIntent().getExtras().getString(Const.TYPE);
            youtubeid = getIntent().getExtras().getString(Const.YOUTUBE_ID);
        }

        streamResponseList = new ArrayList();
        substreamResponseList = new ArrayList();
        specializationResponseList = new ArrayList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        streamResponseList = new ArrayList();
        substreamResponseList = new ArrayList();
        specializationResponseList = new ArrayList();
    }

    @Override
    protected boolean addBackButton() {
        return true;
    }

    @Override
    protected Fragment getFragment() {
        switch (frag_type) {
            case Const.POST_FRAG:
                setTitle(getString(R.string.new_post));
                return NewPostFragment.newInstance(post);
            case Const.COMMENT:
                setTitle(getString(R.string.comments));
                return CommonFragment.newInstance(post, frag_type, postid);
            case Const.NOTIFICATION:
                setTitle(getString(R.string.notifications));
                return CommonFragment.newInstance(post, frag_type, postid);
            case Const.REGISTRATION:
                setTitle(getString(R.string.professional_stream));
                return RegistrationFragment.newInstance(RegType);
            case Const.YOUTUBE:
                return YouTubeFragment.newInstance(youtubeid);
        }
        return null;
    }

    @Override
    public void ontagSelected(Tags tag) {
        ((NewPostFragment) mFragment).tagTV.setVisibility(View.VISIBLE);
        ((NewPostFragment) mFragment).tagTV.setText(tag.getText());
        ((NewPostFragment) mFragment).post_tag_id = tag.getId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (frag_type.equals(Const.NOTIFICATION)) {
            getMenuInflater().inflate(R.menu.notification_menu, menu);
            MenuItem item = menu.getItem(0);
            SpannableString spanString = new SpannableString(menu.getItem(0).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
            return true;
        } else
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.readallIM:
                // Not implemented here
                return false;
            default:
                break;
        }

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.mFragment instanceof NewPostFragment) {
            this.mFragment.onActivityResult(requestCode, resultCode, data);
        } else if (this.mFragment instanceof RegistrationFragment) {
            this.mFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void addDeletedIdToMeta(String id) {
        if (TextUtils.isEmpty(deleted_meta_ids)) deleted_meta_ids = id;
        else deleted_meta_ids = deleted_meta_ids + "," + id;
    }
}
