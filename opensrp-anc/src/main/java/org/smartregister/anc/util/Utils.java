package org.smartregister.anc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.vijay.jsonwizard.widgets.DatePickerFactory;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.event.BaseEvent;
import org.smartregister.domain.jsonmapping.LoginResponseData;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ndegwamartin on 14/03/2018.
 */

public class Utils {

    private static final String TAG = Utils.class.getCanonicalName();
    public static final SimpleDateFormat DB_DF = new SimpleDateFormat(Constants.SQLITE_DATE_TIME_FORMAT);
    public static final SimpleDateFormat CONTACT_DF = new SimpleDateFormat(Constants.CONTACT_DATE_FORMAT);
    public static final SimpleDateFormat CONTACT_SUMMARY_DF = new SimpleDateFormat(Constants.CONTACT_SUMMARY_DATE_FORMAT);
    private static final DateTimeFormatter SQLITE_DATE_DF = DateTimeFormat.forPattern(Constants.SQLITE_DATE_TIME_FORMAT);
    private AllSharedPreferences allSharedPreferences;

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";
    public static final String FACILITY = "Facility";
    public static final String HOME_ADDRESS = "Home Address";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }

    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    public static void saveLanguage(String language) {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(AncApplication
                .getInstance().getApplicationContext()));
        allSharedPreferences.saveLanguagePreference(language);
        setLocale(new Locale(language));
    }


    public static String getLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(AncApplication
                .getInstance().getApplicationContext()));
        return allSharedPreferences.fetchLanguagePreference();
    }

    public static void setLocale(Locale locale) {
        Resources resources = AncApplication.getInstance().getApplicationContext().getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
            AncApplication.getInstance().getApplicationContext().createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, displayMetrics);
        }
    }

    public static void postEvent(BaseEvent event) {
        EventBus.getDefault().post(event);
    }

    public static void postStickyEvent(BaseEvent event) {//Each Sticky event must be manually cleaned by calling Utils.removeStickyEvent after
        // handling
        EventBus.getDefault().postSticky(event);
    }

    public static void removeStickyEvent(BaseEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void writePrefString(Context context, final String key, final String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getDuration(String date) {
        DateTime duration;
        if (StringUtils.isNotBlank(date)) {
            try {
                duration = new DateTime(date);
                return DateUtil.getDuration(duration);
            } catch (Exception e) {
                Log.e(TAG, e.toString(), e);
            }
        }
        return "";
    }

    public static String getDob(int age) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -age);
        cal.set(Calendar.DAY_OF_MONTH, 1);


        cal.set(Calendar.MONTH, 0);

        return DatePickerFactory.DATE_FORMAT.format(cal.getTime());
    }

    public static int convertDpToPx(Context context, int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

    public static String convertDateFormat(Date date, SimpleDateFormat formatter) {

        return formatter.format(date);
    }

    public static boolean isEmptyMap(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmptyCollection(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static Date dobStringToDate(String dobString) {
        DateTime dateTime = dobStringToDateTime(dobString);
        if (dateTime != null) {
            return dateTime.toDate();
        }
        return null;
    }

    public static DateTime dobStringToDateTime(String dobString) {
        try {
            if (StringUtils.isBlank(dobString)) {
                return null;
            }
            return new DateTime(dobString);

        } catch (Exception e) {
            return null;
        }
    }

    public static int getAgeFromDate(String dateOfBirth) {
        DateTime date = DateTime.parse(dateOfBirth);
        Years age = Years.yearsBetween(date.toLocalDate(), LocalDate.now());
        return age.getYears();
    }

    public static String getTodaysDate() {
        return convertDateFormat(Calendar.getInstance().getTime(), DB_DF);
    }

    @Nullable
    public static int getAttributeDrawableResource(
            Context context,
            int attributeId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getGestationAgeFromEDDate(String expectedDeliveryDate) {

        LocalDate date = SQLITE_DATE_DF.withOffsetParsed().parseLocalDate(expectedDeliveryDate);

        LocalDate lmpDate = date.minusWeeks(Constants.DELIVERY_DATE_WEEKS);

        Weeks weeks = Weeks.weeksBetween(lmpDate, LocalDate.now());
        return weeks.getWeeks();
    }

    public AllSharedPreferences getAllSharedPreferences() {
        if (allSharedPreferences == null) {
            allSharedPreferences = AncApplication.getInstance().getContext().allSharedPreferences();
        }
        return allSharedPreferences;
    }

    public void setAllSharedPreferences(AllSharedPreferences allSharedPreferences) {
        this.allSharedPreferences = allSharedPreferences;
    }

    protected String getPreferredName() {
        if (getAllSharedPreferences() == null) {
            return null;
        }

        return getAllSharedPreferences().getANMPreferredName(getAllSharedPreferences().fetchRegisteredANM());
    }

    public String getUserInitials() {
        String initials = null;
        String preferredName = getPreferredName();

        if (StringUtils.isNotBlank(preferredName)) {
            String[] preferredNameArray = preferredName.split(" ");
            initials = "";
            if (preferredNameArray.length > 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0)) + String.valueOf(preferredNameArray[1].charAt(0));
            } else if (preferredNameArray.length == 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0));
            }
        }
        return initials;
    }

    public String getName() {
        return getPreferredName();
    }

    public static String getBuildDate(Boolean isShortMonth) {
        String simpleDateFormat = "";
        if (isShortMonth) {
            simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
        } else {
            simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
        }
        return simpleDateFormat;
    }

    public static String getUserDefaultTeamId(LoginResponseData userInfo) {
        try {
            if (userInfo != null && userInfo.team != null && userInfo.team.team != null) {
                return userInfo.team.team.uuid;
            }
        } catch (Exception e) {
            Log.v("Error : ", e.getMessage());
        }

        return null;
    }

    public static String reverseHyphenSeperatedValues(String rawString, String outputSeperator) {
        String resultString = rawString;
        String[] tokenArray = resultString.split("-");
        ArrayUtils.reverse(tokenArray);
        resultString = StringUtils.join(tokenArray, outputSeperator);

        return resultString;
    }

    public static List<String> getListFromString(String stringArray) {
        return new ArrayList<>(Arrays.asList(stringArray.substring(1, stringArray.length() - 1).replaceAll("\"", "").split(", ")));
    }
}
