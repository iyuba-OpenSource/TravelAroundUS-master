package com.iyuba.concept2.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.concept2.R;
import com.iyuba.concept2.manager.DownloadStateManager;
import com.iyuba.concept2.sqlite.mode.Book;
import com.iyuba.concept2.sqlite.mode.DownloadInfo;
import com.iyuba.concept2.sqlite.mode.Voa;
import com.iyuba.concept2.sqlite.op.BookOp;
import com.iyuba.concept2.sqlite.op.DownloadInfoOp;
import com.iyuba.concept2.sqlite.op.VoaOp;
import com.iyuba.concept2.util.FileDownloader;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.widget.RoundProgressBar;
import com.iyuba.core.lil.user.UserInfoManager;
import com.iyuba.core.me.activity.goldvip.VipCenterGoldActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章列表适配器
 */
public class BookDownloadAdapter extends BaseAdapter {
	private Context mContext;

	public FileDownloader fileDownloader;
	public DownloadStateManager downloadStateManager;
	public List<DownloadInfo> infoList;
	public List<Book> bookList;

	public ViewHolder currViewHolder;
	private ViewHolder viewHolder;
	private VoaOp voaOp;
	private BookOp bookOp;
	private DownloadInfoOp downloadInfoOp;

	public BookDownloadAdapter() {

	}

	public BookDownloadAdapter(Context context) {
		mContext = context;
		downloadStateManager = DownloadStateManager.getInstance();
		fileDownloader = FileDownloader.getInstance();
		bookOp = downloadStateManager.bookOp;
		downloadInfoOp = downloadStateManager.downloadInfoOp;
		infoList = downloadStateManager.downloadList;
		bookList = downloadStateManager.bookList;
		init();
	}

	public void addList(List<Book> booksTemps) {
		bookList.addAll(booksTemps);
	}

	private void init() {
		voaOp = new VoaOp(mContext);
	}

	public void clearList() {
		bookList.clear();
	}

	@Override
	public int getCount() {
		return bookList.size();
	}

	@Override
	public Book getItem(int position) {
		return bookList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Book curBook = bookList.get(position);

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.griditem_download, null);

			viewHolder = new ViewHolder();
			viewHolder.downloadLayout = convertView
					.findViewById(R.id.download_layout);
			viewHolder.downloadedImage = (ImageView) convertView
					.findViewById(R.id.image_downloaded);
			viewHolder.mCircleProgressBar = (RoundProgressBar) convertView
					.findViewById(R.id.roundBar1);
			viewHolder.downloadNum = (TextView) convertView
					.findViewById(R.id.download_num);
			viewHolder.bookName = (TextView) convertView
					.findViewById(R.id.book_name);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.downloadNum.setText(curBook.downloadNum + "/"
				+ curBook.totalNum);
		viewHolder.bookName.setText(curBook.bookName);

		if (curBook.downloadNum >= curBook.totalNum) { // 下载结束
			curBook.downloadState = 2;
			bookOp.updateDownloadState(curBook.bookId, 2);
		}
		
		if (curBook.downloadState == 2) {
			viewHolder.downloadLayout.setVisibility(View.GONE);
			viewHolder.downloadedImage.setVisibility(View.VISIBLE);
		} else {
			viewHolder.downloadedImage.setVisibility(View.GONE);
			viewHolder.downloadLayout.setVisibility(View.VISIBLE);

			// 如果下载列表中存在
			if(curBook.downloadState == 1) {
				DownloadInfo curInfo = fileDownloader.curInfo;
				if ((curInfo != null)
						&& (curBook.bookId / 1000) == (curInfo.voaId / 1000)) {
					viewHolder.mCircleProgressBar
							.setCricleProgressColor(0xff00AEFF);
					viewHolder.mCircleProgressBar.setMax(100);
					viewHolder.mCircleProgressBar.setProgress(curInfo.downloadPer);
				} 
			} else {
				viewHolder.mCircleProgressBar
					.setCricleProgressColor(0xff00AEFF);
				viewHolder.mCircleProgressBar.setMax(1);
				viewHolder.mCircleProgressBar.setProgress(0);
			}

			switch (curBook.downloadState) {
			case -2:
				viewHolder.mCircleProgressBar
						.setBackgroundResource(R.drawable.wait_download);
				break;
			case -1:
				viewHolder.mCircleProgressBar
						.setBackgroundResource(R.drawable.pause_download);
				break;
			case 0:
				viewHolder.mCircleProgressBar
						.setBackgroundResource(R.drawable.pause_download);
				break;
			case 1:
				viewHolder.mCircleProgressBar
						.setBackgroundResource(R.drawable.download);
				break;
			}
		}

		viewHolder.downloadLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
//				if (!isOpenNetwork()) {
//					// 请检查网络
//					CustomToast.showToast(mContext,
//							R.string.category_check_network, 1000);
//				} else {
					final int bookId = curBook.bookId;

					if (curBook.downloadState == 0) {

						bookOp.updateDownloadState(curBook.bookId, -2);

						if (UserInfoManager.getInstance().isVip()) {
							if (fileDownloader.getDownloadState() == 0) {
								curBook.downloadState = 1;
							} else {
								curBook.downloadState = -2;
							}
							notifyDataSetChanged();
							
							Message msg = handler.obtainMessage();
							msg.what = 1;
							msg.arg1 = bookId;
							handler.sendMessage(msg);
						} else {
							final int downloadNum = getDownloadNum(bookId);
							Log.e("bookId", bookId + "");
							Log.e("downloadNum", downloadNum + "");
							
							AlertDialog alert = new AlertDialog.Builder(
									mContext).create();
							alert.setTitle(mContext.getResources().getString(
									R.string.alert));
							alert.setMessage(mContext.getResources().getString(
									R.string.nladapter_notvip));
							alert.setIcon(android.R.drawable.ic_dialog_alert);
							alert.setButton(
									AlertDialog.BUTTON_POSITIVE,
									mContext.getResources().getString(
											R.string.alert_btn_buy),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = new Intent();
											intent.setClass(mContext,
													VipCenterGoldActivity.class);
											mContext.startActivity(intent);
										}
									});
							alert.setButton(
									AlertDialog.BUTTON_NEGATIVE,
									mContext.getResources().getString(
											R.string.alert_btn_cancel),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											if(downloadNum < 10) {
												Log.e("downloadNum", downloadNum + "");
												
												if (fileDownloader.getDownloadState() == 0) {
													curBook.downloadState = 1;
												} else {
													curBook.downloadState = -2;
												}
												notifyDataSetChanged();
												
												Message msg = handler.obtainMessage();
												msg.what = 2;
												msg.arg1 = bookId;
												handler.sendMessage(msg);
											} 
										}
									});
							alert.show();
						}
					} else if(curBook.downloadState == -1) {
						
						if (fileDownloader.getDownloadState() == 0) {
							curBook.downloadState = 1;
						} else {
							curBook.downloadState = -2;
						}
						Log.e("downloadState", curBook.downloadState + "");
						notifyDataSetChanged();
						
						Message msg = handler.obtainMessage();
						msg.what = 3;
						msg.arg1 = bookId;
						handler.sendMessage(msg);
						
					} else if (curBook.downloadState == 1
							|| curBook.downloadState == -2) {
						curBook.downloadState = -1;
						notifyDataSetChanged();

						updateStateToStop(bookId);
						bookOp.updateDownloadState(curBook.bookId, -1);
					}
				}
//			}
		});

		currViewHolder = viewHolder;
		return convertView;
	}

	public void updateStateToWait(int bookId) {
		int bookIndex = bookId / 1000;
		for (DownloadInfo downloadInfo : infoList) {
			if (downloadInfo.voaId / 1000 == bookIndex) {
				if (downloadInfo.downloadedState == 0
						|| downloadInfo.downloadedState == -1) {
					downloadInfo.downloadedState = -2;
				}
			}
		}
	}

	public void updateStateToStop(int bookId) {
		int bookIndex = bookId / 1000;
		for (DownloadInfo downloadInfo : infoList) {
			if (downloadInfo.voaId / 1000 == bookIndex) {
				if (downloadInfo.downloadedState == 1
						|| downloadInfo.downloadedState == -2) {
					downloadInfo.downloadedState = -1;
				}
			}
		}
	}

	private boolean isOpenNetwork() {
		ConnectivityManager connManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connManager.getActiveNetworkInfo() != null) {
			return connManager.getActiveNetworkInfo().isAvailable();
		}

		return false;
	}

	public DownloadInfo getDownloadInfo(int voaId) {
		for (DownloadInfo tempInfo : infoList) {
			if (tempInfo.voaId == voaId) {
				return tempInfo;
			}
		}

		return null;
	}

	private void downloadForVip(int bookId) {
		List<Voa> voaList = voaOp.findDataByBook(bookId / 1000);
		List<DownloadInfo> infoList = new ArrayList<DownloadInfo>();
		DownloadInfo info = null;

		for (Voa voa : voaList) {
			voa.isDownload = "1";
			voaOp.insertDataToDownload(voa.voaId);

			info = getDownloadInfo(voa.voaId);

			if (info == null) {
				info = new DownloadInfo(voa.voaId);
				infoList.add(info);
			}
		}
		downloadInfoOp.insert(infoList);
		fileDownloader.updateInfoList(infoList);
	}

	private void download(int bookId, int downloadNum) {
		List<Voa> voaList = voaOp.findDataByBook(bookId / 1000);
		List<DownloadInfo> infoList = new ArrayList<DownloadInfo>();
		DownloadInfo info = null;

		for (Voa voa : voaList) {
			info = getDownloadInfo(voa.voaId);

			if (info == null) {
				voa.isDownload = "1";
				voaOp.insertDataToCollection(voa.voaId);
				info = new DownloadInfo(voa.voaId);
				infoList.add(info);

				downloadNum++;
			} else if(info.downloadedState == 0) {
				voa.isDownload = "1";
				voaOp.insertDataToCollection(voa.voaId);
				info.downloadedState = -2;
				
				downloadNum++;
			}

			if (downloadNum >= 10) {
				break;
			}
		}
		downloadInfoOp.insert(infoList);
		fileDownloader.updateInfoList(infoList);
	}
	
	public void restartDownload(int bookId) {
		int bookIndex = bookId / 1000;
		
		for(DownloadInfo info : infoList) {
			if(info.voaId / 1000 == bookIndex) {
				if(info == fileDownloader.curInfo) {
					info.downloadedState = 1;
				} else if(info.downloadedState == -1) {
					info.downloadedState = -2;
				}
			}
		}
		
		fileDownloader.updateInfoList();
	}

	public int getDownloadNum(int bookId) {
		int bookIndex = bookId / 1000;
		int downloadNum = 0;

		for (DownloadInfo info : infoList) {
			if (info.voaId / 1000 == bookIndex && info.downloadedState != 0) {
				downloadNum++;
			}
		}

		return downloadNum;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			int bookId = 0;
			int downloadNum = 0;
			
			switch (msg.what) {
			case 0:
				notifyDataSetChanged();
				break;
			case 1:
				bookId = msg.arg1;
				downloadForVip(bookId);
				updateStateToWait(bookId);
				break;
			case 2:
				bookId = msg.arg1;
				downloadNum = msg.arg2;
				download(bookId, downloadNum);
				updateStateToWait(bookId);
				break;
			case 3:
				bookId = msg.arg1;
				restartDownload(bookId);
				break;
			default:
				break;
			}
		}
	};

	public class ViewHolder {
		View downloadLayout;
		RoundProgressBar mCircleProgressBar;// 进度环
		ImageView downloadedImage;// 下载完成的标示
		TextView downloadNum;
		TextView bookName;
	}
}
