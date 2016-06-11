package br.com.dedoduro;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.dedoduro.callback.AntesDeExecutar;
import br.com.dedoduro.callback.DepoisDeExecutar;
import br.com.dedoduro.model.Denuncia;
import br.com.dedoduro.model.TipoDenuncia;

public class MainActivity extends AppCompatActivity implements LocationListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;

    private Spinner tipo;

    private EditText observacao;

    private ImageView imagemLogo;

    private LinearLayout layout;

    private ProgressDialog dialog;

    byte[] byteArray;
    private String lat;
    private String lng;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public boolean verificaConexao() {
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            byteArray = stream.toByteArray();

            imageView.setImageBitmap(imageBitmap);

            imagemLogo.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        }
    }

    public void getLocalizacao() {
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Location location = null;
            if (isGPSEnabled || isNetworkEnabled) {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        setLatAndLng(location);
                    }
                }
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            setLatAndLng(location);
                        }
                    }
                }
            }

        } catch (Exception ignore) {
        }
    }

    private void setLatAndLng(Location location) {
        lat = "" + location.getLatitude();
        lng = "" + location.getLongitude();
    }

    @Override
    public void onLocationChanged(Location location) {
        setLatAndLng(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void openDialog() {
        dialog = ProgressDialog.show(MainActivity.this, "", "Aguarde. Sua denuncia esta sendo enviada...", true);
    }

    private void closeDialog() {
        dialog.dismiss();
    }

    public TipoDenuncia getForLabel(String label) {
        for (TipoDenuncia tipoDenuncia : TipoDenuncia.values()) {
            if (tipoDenuncia.getLabel().equals(label)) {
                return tipoDenuncia;
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imagemLogo = (ImageView) findViewById(R.id.imagemLogo);
        tipo = (Spinner) findViewById(R.id.tipo);
        layout = (LinearLayout) findViewById(R.id.layout);
        imageView = (ImageView) findViewById(R.id.imagem);
        observacao = (EditText) findViewById(R.id.observacao);
        final Button enviar = (Button) findViewById(R.id.enviar);
        final Button limpar = (Button) findViewById(R.id.limpar);

        createTipoDenuncia();
        getLocalizacao();

        imagemLogo.setOnClickListener(clickImagemLogo());
        enviar.setOnClickListener(enviarClick());
        limpar.setOnClickListener(limparClick());
    }

    @NonNull
    private View.OnClickListener clickImagemLogo() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        };
    }

    @NonNull
    private View.OnClickListener limparClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpar();
            }
        };
    }

    @NonNull
    private View.OnClickListener enviarClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao()) {
                    Denuncia denuncia = new Denuncia();
                    denuncia.setFoto(Base64.encodeToString(byteArray, Base64.DEFAULT));
                    // TODO obter numero de telefone do usuário
                    denuncia.setNumeroTelefone("0123456789");
                    denuncia.setObservacao(observacao.getText().toString());
                    denuncia.setTipoDenuncia(getForLabel(tipo.getSelectedItem().toString()));
                    denuncia.setLatitude(lat);
                    denuncia.setLongitude(lng);

                    new MyTask(denuncia, new AntesDeExecutar() {
                        @Override
                        public void execute() {
                            openDialog();
                        }
                    }, new DepoisDeExecutar() {
                        @Override
                        public void execute(boolean enviadoComSucesso) {
                            closeDialog();
                            if (enviadoComSucesso) {
                                limpar();

                                exibeAlerta("Obrigado!!!", "Denuncia enviada com sucesso");
                            } else {
                                exibeAlerta("Erro", "Não conseguimos enviar sua denúncia");
                            }
                        }
                    }).execute();
                } else {
                    exibeAlerta("Falha de conexão", "Seu dispositivo não está conectado na internet");
                }
            }
        };
    }

    private void exibeAlerta(String titulo, String mensagem) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void createTipoDenuncia() {
        List<String> denuncias = new ArrayList<>();
        for (TipoDenuncia tipoDenuncia : TipoDenuncia.values()) {
            denuncias.add(tipoDenuncia.getLabel());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, denuncias);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipo.setAdapter(dataAdapter);
    }

    private void limpar() {
        observacao.setText("");
        tipo.setSelection(0);
        imageView.setImageResource(android.R.color.transparent);
        byteArray = null;
        layout.setVisibility(View.GONE);
        imagemLogo.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
        }

        return super.onOptionsItemSelected(item);
    }
}
