package a3wish.main.com.aims212.sam.a3wish.upload;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import a3wish.main.com.aims212.sam.a3wish.member.UpdateMemberActivity;

/**
 * An asynchronous task that handles the Drive API call.
 * Placing the API calls in their own task ensures the UI stays responsive.
 */
public class ApiAsyncTaskVideo extends AsyncTask<Void, Void, Void> {
    private UploadVideoActivity mActivity;

    /**
     * Constructor.
     * @param mActivity MainActivity that spawned this task.
     */
    ApiAsyncTaskVideo(UploadVideoActivity mActivity) {
        this.mActivity = mActivity;
    }





    /**
     * Background task to call Drive API.
     * @param params no parameters needed for this task.
     */
    @Override
    protected Void doInBackground(Void... params) {
        try {
//            test.clearResultsText();
//            test.updateResultsText(getDataFromApi());
            getDataFromApi();


            mActivity.checkDriveServer=true;
        } catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
            mActivity.showGooglePlayServicesAvailabilityErrorDialog(
                    availabilityException.getConnectionStatusCode());
            System.out.println("GooglePlayServicesAvailabilityIOException:"+availabilityException.toString());
            Log.e("GooglePlayServicesAvailabilityIOException",availabilityException.toString());
            mActivity.checkDriveServer=false;

        } catch (UserRecoverableAuthIOException userRecoverableException) {
            mActivity.startActivityForResult(
                    userRecoverableException.getIntent(),
                    mActivity.REQUEST_AUTHORIZATION);

            System.out.println("UserRecoverableAuthIOException:"+userRecoverableException.toString());
            Log.e("UserRecoverableAuthIOException",userRecoverableException.toString());

            mActivity.checkDriveServer=false;

        } catch (Exception e) {
//            mActivity.updateStatus("The following error occurred:\n" +
//                    e.getMessage());
            System.out.println("Exception:"+e.toString());
            Log.e("Exception",e.toString());

            mActivity.checkDriveServer=false;

            mActivity.showDialogOK("您的Google帳號無效，需更換，否則無法使用",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    mActivity.startActivity(new Intent(mActivity, UpdateMemberActivity.class));
                                    mActivity.finish();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    mActivity.finish();
                                    break;
                            }
                        }
                    });


        }
        if (mActivity.mProgress.isShowing()) {
            mActivity.mProgress.dismiss();
        }
        return null;
    }



    /**
     * Fetch a list of up to 10 file names and IDs.
     * @return List of Strings describing files, or an empty list if no files
     *         found.
     * @throws IOException
     */
    private List<String> getDataFromApi() throws IOException {
        // Get a list of up to 10 files.
        List<String> channelInfo = new ArrayList<String>();
        ChannelListResponse result = mActivity.mService.channels().list("snippet,contentDetails,statistics")
                .setForUsername("GoogleDevelopers")
                .execute();
        List<Channel> channels = result.getItems();
        if (channels != null) {
            Channel channel = channels.get(0);
            channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                    "Its title is '" + channel.getSnippet().getTitle() + ", " +
                    "and it has " + channel.getStatistics().getViewCount() + " views.");
        }
        return channelInfo;
    }



}