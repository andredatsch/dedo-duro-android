package br.com.dedoduro;

import android.os.AsyncTask;

import br.com.dedoduro.callback.AntesDeExecutar;
import br.com.dedoduro.callback.DepoisDeExecutar;
import br.com.dedoduro.model.Denuncia;
import br.com.dedoduro.service.DenunciaService;
import br.com.dedoduro.service.impl.DenunciaServiceImpl;

public class MyTask extends AsyncTask<Void, Integer, Void> {

    private Denuncia denuncia;

    private AntesDeExecutar antesDeExecutar;

    private DepoisDeExecutar depoisDeExecutar;

    private boolean enviadoComSucesso;

    public MyTask(Denuncia denuncia, AntesDeExecutar antesDeExecutar, DepoisDeExecutar depoisDeExecutar) {
        this.denuncia = denuncia;
        this.antesDeExecutar = antesDeExecutar;
        this.depoisDeExecutar = depoisDeExecutar;
    }

    @Override
    protected Void doInBackground(Void... params) {
        DenunciaService denunciaService = new DenunciaServiceImpl();
        enviadoComSucesso = denunciaService.send(denuncia);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        antesDeExecutar.execute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        depoisDeExecutar.execute(enviadoComSucesso);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        depoisDeExecutar.execute(enviadoComSucesso);
    }

}