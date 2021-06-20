package gg.nils.semanticrelease.api.versioncontrol.converter;

public interface Converter<From, To> {

    To convert(From from);
}
