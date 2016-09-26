package com.example.wu_.csnet;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private String privider;
    private EditText editText;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.location);
        listView= (ListView) findViewById(R.id.listView);
        GetGps();
        GetWifi();
    }

    public void GetGps() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateView(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateView(location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                }//获得权限
                updateView(locationManager.getLastKnownLocation(provider));
            }
            @Override
            public void onProviderDisabled(String provider) {
                updateView(null);
            }
        });
    }
   public void updateView(Location location){
       if(location!=null){
           StringBuffer sb = new StringBuffer();
           sb.append("经度：");
           sb.append(location.getLongitude());
           sb.append("\n纬度：");
           sb.append(location.getLatitude());
           sb.append("\n高度：");
           sb.append(location.getAltitude());
           sb.append("  速度：");
           sb.append(location.getSpeed());
           sb.append("\n方向：");
           sb.append(location.getBearing());
           sb.append("  精度：");
           sb.append(location.getAccuracy());
           editText.setText(sb.toString());
       }
       else
           editText.setText("nothing");
   }

    public void GetWifi(){
        final WifiManager wifi= (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //WifiInfo wifiInfo=wifi.getConnectionInfo();
        final ArrayList<ScanResult> list;                   //存放周围wifi热点对象的列表
        list = (ArrayList<ScanResult>) wifi.getScanResults();
        if (list == null) {
            Toast.makeText(this, "wifi未连接！", Toast.LENGTH_LONG).show();
        }else {
            listView.setAdapter(new MyAdapter(MainActivity.this,list));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ScanResult scanResult = list.get(position);
                WifiInfo wifiInfor = wifi.getConnectionInfo();
                wifiInfor.getMacAddress();
                Toast.makeText(MainActivity.this,
                        "Mac地址："+scanResult.BSSID, Toast.LENGTH_LONG).show();
            }
        });
    }
    //将搜索到的wifi根据信号强度从强到弱进行排序
    private void sortByLevel(ArrayList<ScanResult> list) {
        for(int i=0;i<list.size();i++)
            for(int j=1;j<list.size();j++)
            {
                if(list.get(i).level<list.get(j).level)    //level属性即为强度
                {
                    ScanResult temp = null;
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
    }
    public class MyAdapter extends BaseAdapter {
        LayoutInflater inflater;
        List<ScanResult> list;
        public MyAdapter(Context context, List<ScanResult> list) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            view = inflater.inflate(R.layout.item_wifi_list, null);

            ScanResult scanResult = list.get(position);
            TextView textView = (TextView) view.findViewById(R.id.textView);
            textView.setText(scanResult.SSID);
            TextView signalStrenth = (TextView) view.findViewById(R.id.signal_strenth);
            signalStrenth.setText(String.valueOf(Math.abs(scanResult.level)));
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            if (Math.abs(scanResult.level) > 100) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi));
            } else if (Math.abs(scanResult.level) > 80) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi_0));
            } else if (Math.abs(scanResult.level) > 70) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi_0));
            } else if (Math.abs(scanResult.level) > 60) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi_1));
            } else if (Math.abs(scanResult.level) > 50) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi_2));
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.wifi_3));
            }
            return view;
        }
    }

}
