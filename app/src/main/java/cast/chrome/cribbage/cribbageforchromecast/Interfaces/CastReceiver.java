package cast.chrome.cribbage.cribbageforchromecast.Interfaces;

import org.json.JSONObject;
import org.json.JSONException;

/**
 * Created by milord on 28-Nov-14.
 */
public interface CastReceiver {
    public void onReceiveJSON(JSONObject jsonObject) throws Exception;
}
