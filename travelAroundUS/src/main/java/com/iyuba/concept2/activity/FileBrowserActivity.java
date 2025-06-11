package com.iyuba.concept2.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.adapter.FileAdapter;
import com.iyuba.concept2.sqlite.mode.FileInfo;
import com.iyuba.concept2.util.FileActivityHelper;
import com.iyuba.concept2.util.FileUtil;
import com.iyuba.concept2.util.PasteFile;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.widget.dialog.CustomToast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileBrowserActivity extends ListActivity {
	private TextView _filePath;
	private Button func, back;
	private List<FileInfo> _files = new ArrayList<FileInfo>();
	private String _rootPath = FileUtil.getSDPath();
	private String _currentPath = ConfigManager.Instance().loadString(
			"media_saving_path");
	private final String TAG = "Main";
	private final int MENU_RENAME = Menu.FIRST;
	private final int MENU_COPY = Menu.FIRST + 3;
	private final int MENU_MOVE = Menu.FIRST + 4;
	private final int MENU_DELETE = Menu.FIRST + 5;
	private final int MENU_INFO = Menu.FIRST + 6;
	private BaseAdapter adapter = null;
	private Context mContext;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_browser_main);
		CrashApplication.getInstance().addActivity(this);
		mContext = this;
		_filePath = (TextView) findViewById(R.id.file_path);
		func = (Button) findViewById(R.id.func);
		back = (Button) findViewById(R.id.button_back);

		registerForContextMenu(getListView());

		adapter = new FileAdapter(this, _files);
		setListAdapter(adapter);

		func.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openOptionsMenu();
			}
		});
		
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				File f = new File(_currentPath);
				String parentPath = f.getParent();
				if (parentPath != null) {
					viewFiles(parentPath);
				} else {
					exit();
				}
			}
		});

		File file = new File(_currentPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		viewFiles(file.getParent());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo info = null;

		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfo", e);
			return;
		}

		FileInfo f = _files.get(info.position);
		menu.setHeaderTitle(f.Name);
		menu.add(0, MENU_RENAME, 1, getString(R.string.file_rename));
		menu.add(0, MENU_COPY, 2, getString(R.string.file_copy));
		menu.add(0, MENU_MOVE, 3, getString(R.string.file_move));
		menu.add(0, MENU_DELETE, 4, getString(R.string.file_delete));
		menu.add(0, MENU_INFO, 5, getString(R.string.file_info));
	}

	/** �����Ĳ˵��¼����� **/
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		FileInfo fileInfo = _files.get(info.position);
		File f = new File(fileInfo.Path);
		switch (item.getItemId()) {
		case MENU_RENAME:
			FileActivityHelper.renameFile(FileBrowserActivity.this, f,
					renameFileHandler);
			return true;
		case MENU_COPY:
			pasteFile(f.getPath(), "COPY");
			return true;
		case MENU_MOVE:
			pasteFile(f.getPath(), "MOVE");
			return true;
		case MENU_DELETE:
			FileUtil.deleteFile(f);
			viewFiles(_currentPath);
			return true;
		case MENU_INFO:
			FileActivityHelper.viewFileInfo(FileBrowserActivity.this, f);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		FileInfo f = _files.get(position);

		if (f.IsDirectory) {
			viewFiles(f.Path);
		} else {
			openFile(f.Path);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			File f = new File(_currentPath);
//			String parentPath = f.getParent();
//			if (parentPath != null) {
//				viewFiles(parentPath);
//			} else {
//				exit();
//			}
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK == resultCode) {
			Bundle bundle = data.getExtras();
			if (bundle != null && bundle.containsKey("CURRENTPATH")) {
				viewFiles(bundle.getString("CURRENTPATH"));
			}
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mainmenu_home:
			viewFiles(_rootPath);
			break;
		case R.id.mainmenu_refresh:
			viewFiles(_currentPath);
			break;
		case R.id.mainmenu_createdir:
			FileActivityHelper.createDir(FileBrowserActivity.this,
					_currentPath, createDirHandler);
			break;
		case R.id.mainmenu_exit:
			exit();
			break;
		default:
			break;
		}
		return true;
	}

	private void viewFiles(String filePath) {
		ArrayList<FileInfo> tmp = FileActivityHelper.getFiles(
				FileBrowserActivity.this, filePath);
		if (tmp != null) {
			_files.clear();
			_files.addAll(tmp);
			tmp.clear();

			_currentPath = filePath;
			_filePath.setText(filePath);

			adapter.notifyDataSetChanged();
		}
	}

	private void openFile(String path) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		File f = new File(path);
		String type = FileUtil.getMIMEType(f.getName());
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}

	private final Handler renameFileHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};

	private final Handler createDirHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0)
				viewFiles(_currentPath);
		}
	};

	private void pasteFile(String path, String action) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("CURRENTPASTEFILEPATH", path);
		bundle.putString("ACTION", action);
		intent.putExtras(bundle);
		intent.setClass(FileBrowserActivity.this, PasteFile.class);
		startActivityForResult(intent, 0);
	}

	private void savePath() {
		ConfigManager.Instance().putString("media_saving_path",
				_filePath.getText().toString());
		Constant.videoAddr = _filePath.getText().toString();
		Intent intent = new Intent();
		intent.putExtra("nowSavingPath", _filePath.getText().toString());
		setResult(2, intent);
		Log.d("path", _filePath.getText().toString());
	}

	private void exit() {

		new AlertDialog.Builder(FileBrowserActivity.this)
				.setMessage(R.string.confirm_exit)
				.setCancelable(false)
				.setPositiveButton(R.string.mainmenu_path_save,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (!(new File(_filePath.getText().toString())
										.canWrite())) {
									CustomToast.showToast(mContext,
											R.string.file_path_ro, 1000);
									return;
								}
								savePath();
								FileBrowserActivity.this.finish();
							}
						})
				.setNegativeButton(R.string.mainmenu_exitnow,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								FileBrowserActivity.this.finish();
							}
						}).show();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
