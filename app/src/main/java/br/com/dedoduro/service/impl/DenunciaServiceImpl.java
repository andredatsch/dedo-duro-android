package br.com.dedoduro.service.impl;

import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.dedoduro.model.Denuncia;
import br.com.dedoduro.service.DenunciaService;

/**
 * Created by gasparbarancelli on 11/06/16.
 */
public class DenunciaServiceImpl implements DenunciaService {

    @Override
    public boolean send(Denuncia denuncia) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            restTemplate.postForObject("http://52.25.24.139:8746/denuncia", denuncia, Denuncia.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
