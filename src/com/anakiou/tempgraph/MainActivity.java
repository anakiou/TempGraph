package com.anakiou.tempgraph;

import java.util.Calendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.LinearLayout;

/**
 * PieChartDemo1Activity
 */
public class MainActivity extends Activity implements SensorEventListener {
	private GraphicalView mChart;
	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private XYSeries mTempSeries;
	private XYSeries mHumSeries;
	private XYSeriesRenderer mTempRenderer;
	private XYSeriesRenderer mHumRenderer;

	private SensorManager mSensorManager;
	private Sensor mTemperature;
	private Sensor mHumidity;

	private void initChart() {
		mTempSeries = new XYSeries("Ambient Temperature");
		mHumSeries = new XYSeries("Humidity");
		
		mDataset.addSeries(mTempSeries);
		mDataset.addSeries(mHumSeries);
		
		mTempRenderer = new XYSeriesRenderer();
		mTempRenderer.setColor(Color.RED);
		mTempRenderer.setPointStyle(PointStyle.TRIANGLE);
		
		mHumRenderer = new XYSeriesRenderer();
		mHumRenderer.setColor(Color.BLUE);
		mHumRenderer.setPointStyle(PointStyle.DIAMOND);

		mRenderer.setAxisTitleTextSize(40);
		mRenderer.setChartTitleTextSize(40);
		mRenderer.setLabelsTextSize(40);
		mRenderer.setLegendTextSize(40);
		mRenderer.setPointSize(5f);
		mRenderer.setMargins(new int[] { 50, 100, 100, 10 });
		mRenderer.setChartTitle("Environmental");
		mRenderer.setXTitle("Second");
		mRenderer.setYTitle("Temperature/Humidity");
		mRenderer.setXAxisMin(0);
		mRenderer.setXAxisMax(60);
		mRenderer.setYAxisMin(10);
		mRenderer.setYAxisMax(100);
		mRenderer.setAxesColor(Color.LTGRAY);
		mRenderer.setLabelsColor(Color.LTGRAY);
		mRenderer.setXLabels(6);
		mRenderer.setYLabels(10);
		mRenderer.setShowGrid(true);
		mRenderer.setGridColor(Color.BLACK);
		mRenderer.setXLabelsAlign(Align.RIGHT);
		mRenderer.setYLabelsAlign(Align.RIGHT);
		mRenderer.setZoomButtonsVisible(true);
		
		mRenderer.addSeriesRenderer(mTempRenderer);
		mRenderer.addSeriesRenderer(mHumRenderer);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mTemperature = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		mHumidity= mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
	}

	@Override
	public final void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do something here if sensor accuracy changes.
	}

	@Override
	public final void onSensorChanged(SensorEvent event) {
		float reading = event.values[0];
		int type = event.sensor.getType();
		Calendar now = Calendar.getInstance();
		if(now.get(Calendar.SECOND) == 0){
			mTempSeries.clear();
			mHumSeries.clear();
		}
		
		
		if (type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
			mTempSeries.add(now.get(Calendar.SECOND), reading);
			mChart.repaint();
		}else if(type == Sensor.TYPE_RELATIVE_HUMIDITY){
			mHumSeries.add(now.get(Calendar.SECOND), reading);
			mChart.repaint();
		}
		
	}

	protected void onResume() {
		super.onResume();
		
		mSensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mHumidity, SensorManager.SENSOR_DELAY_NORMAL);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
		if (mChart == null) {
			initChart();
			mChart = ChartFactory.getCubeLineChartView(this, mDataset, mRenderer, 0.3f);
			layout.addView(mChart);
		} else {
			mChart.repaint();
		}
	}

	@Override
	protected void onPause() {
		// Be sure to unregister the sensor when the activity pauses.
		super.onPause();
		mSensorManager.unregisterListener(this);
	}
}