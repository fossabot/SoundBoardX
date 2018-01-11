package io.github.xtonousou.soundboardx;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.Calendar;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View aboutPage = new AboutPage(AboutActivity.this)
				.isRTL(false)
				.setCustomFont(getString(R.string.roboto_r))
				.setDescription(getString(R.string.app_description))
				.setImage(R.mipmap.ic_launcher)
				.addItem(getPrivacyPolicyElement())
				.addGroup(getString(R.string.connect))
				.addEmail(getString(R.string.mail))
				.addItem(getVersionElement())
				.addItem(getCopyRightsElement())
				.create();

		setContentView(aboutPage);
	}

	Element getPrivacyPolicyElement() {
		IconicsDrawable icon = new IconicsDrawable(this)
				.icon(FontAwesome.Icon.faw_unlock_alt)
				.sizeDp(24);

		Element privacyPolicyElement = new Element();
		privacyPolicyElement.setGravity(Gravity.LEFT);
		privacyPolicyElement.setTitle(getString(R.string.privacy_policy));
		//privacyPolicyElement.setIconDrawable(); // set icon manually if wanted, place it inside
		// hdpi folders

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.example.com"));

		privacyPolicyElement.setOnClickListener(view -> startActivity(intent));
		return privacyPolicyElement;
	}

	Element getVersionElement() {
		String versionName = "";
		String version = getString(R.string.version);
		try {
			PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pInfo.versionName;
		} catch (PackageManager.NameNotFoundException ignore) {}
		Element versionElement = new Element();
		versionElement.setGravity(Gravity.CENTER);
		versionElement.setTitle(String.format(getString(R.string.about_version),
				getString(R.string.app_name), versionName, version));
		versionElement.setOnClickListener(view ->
				Toast.makeText(AboutActivity.this, R.string.about_version_alt, Toast.LENGTH_LONG)
						.show());
		return versionElement;
	}

	Element getCopyRightsElement() {
		Element copyRightsElement = new Element();
		copyRightsElement.setTitle(String.format(getString(R.string.copy_right),
				Calendar.getInstance().get(Calendar.YEAR)));
		copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
		copyRightsElement.setIconNightTint(R.color.colorAccent);
		copyRightsElement.setGravity(Gravity.CENTER);
		copyRightsElement.setOnClickListener(view ->
				Toast.makeText(AboutActivity.this, R.string.license, Toast.LENGTH_SHORT)
						.show());
		return copyRightsElement;
	}
}
