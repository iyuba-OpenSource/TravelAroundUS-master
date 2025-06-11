package com.iyuba.concept2.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iyuba.concept2.R;
import com.iyuba.core.common.base.CrashApplication;

public class SleepDialog extends Activity {

	private LinearLayout layout;
	private Button deButton, inButton, comfiButton, cancelButton;
	private EditText editTextHour, editTextMinute;
	private EditText mEtMin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sleep_dialog);
		CrashApplication.getInstance().addActivity(this);
		deButton = ((Button) findViewById(R.id.sleeptime_de));
		inButton = (Button) findViewById(R.id.sleeptime_in);
		comfiButton = (Button) findViewById(R.id.exitBtn0);
		cancelButton = (Button) findViewById(R.id.exitBtn1);

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("minute", 0);
				setResult(1, intent);
				finish();
			}
		});
		deButton.setText("-");
		inButton.setText("+");
		// 增加和删除按钮的逻辑
		deButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editTextHour.isFocused()) {
					int i = Integer.valueOf(editTextHour.getText().toString()) - 1;
					if (i < 0) {
						i = 0;
					}
					editTextHour.setText(String.format("%02d", i));
				} else {
					int i = Integer
							.valueOf(editTextMinute.getText().toString()) - 10;
					if (i < 0) {
						if (Integer.valueOf(editTextHour.getText().toString()) > 0) {
							editTextHour.setText(String.format("%02d",
									Integer.valueOf(editTextHour.getText()
											.toString()) - 1));
							editTextMinute.setText(String
									.format("%02d", 60 + i));
						} else {
							i = 0;
							editTextMinute.setText(String.format("%02d", i));
						}

					} else {
						editTextMinute.setText(String.format("%02d", i));
					}
				}
			}
		});
		inButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (editTextHour.isFocused()) {
					int i = Integer.valueOf(editTextHour.getText().toString()) + 1;
					editTextHour.setText(String.format("%02d", i));
				} else {
					int i = Integer
							.valueOf(editTextMinute.getText().toString()) + 10;
					if (i >= 60) {

						editTextHour.setText(String.format("%02d", Integer
								.valueOf(editTextHour.getText().toString()) + 1));
						editTextMinute.setText(String.format("%02d", i - 60));
					} else {
						editTextMinute.setText(String.format("%02d", i));
					}

				}
			}
		});
		editTextHour = (EditText) findViewById(R.id.sleeptime_hour);
		editTextMinute = (EditText) findViewById(R.id.sleeptime_minute);

		mEtMin=findViewById(R.id.et_input_min);

		editTextHour.setText("00");
		editTextMinute.setText("00");
		comfiButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("hour",
						Integer.valueOf(editTextHour.getText().toString()));
				int min=0;
				if (!mEtMin.getText().toString().equals("")){
                    min= Integer.valueOf(mEtMin.getText().toString());
                }
				intent.putExtra("minute",min);
				setResult(1, intent);// 第一个参数是自定义的结果编号，可以自己定义static，这里就省了。
				finish();
			}
		});

		editTextHour.setText("00");
		editTextMinute.setText("00");
		// textView.setText("00:00");
		layout = (LinearLayout) findViewById(R.id.exit_layout);
		layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "提示：点击窗口外部关闭窗口！",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return true;
	}

	public void exitbutton0(View v) {
		this.finish();
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
