//	카메라의 서페이스 뷰
// 
//	카메라를 등록하고 관리함

package seoultour.project.team;

import java.io.IOException;
import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {

	private SurfaceHolder mSurfaceHolder;
	private Camera mCamera;

	// 서페이스 뷰를 관리하는 Holder를 설정해줌
	public CameraPreview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		mSurfaceHolder = getHolder();

		// 콜백함수들을 이 클래스의 함수들로 등록함
		mSurfaceHolder.addCallback(this);

		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

	}

	// 서페이스 뷰가 만들어질때 실행됨
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// 카메라를 염
		mCamera = Camera.open();

		try {
			// 카메라 화면이 시간이 지나도 꺼지지 않게 함
			mSurfaceHolder.setKeepScreenOn(true);
			mCamera.setPreviewDisplay(mSurfaceHolder);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			mCamera.release();
			mCamera = null;
			e.printStackTrace();
		}
	}

	// 서페이스뷰가 사이즈등이 바뀌었을때 실행됨
	// 처음 서페이스뷰가 시작될때도 실행됨
	// 카메라의 프리뷰를 시작함
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

		mCamera.startPreview();

	}

	// 서페이스 뷰가 소멸될때 실행됨
	// 카메라를 멈추고 자원을 반환함
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

}
