/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.modules.lookup.annotations.apt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import org.netbeans.modules.lookup.annotations.annotations.Contract;
import org.netbeans.modules.lookup.annotations.annotations.ContractProvided;
import org.netbeans.modules.lookup.annotations.annotations.Service;
import org.netbeans.modules.lookup.annotations.model.ContractM;
import org.netbeans.modules.lookup.annotations.model.ServiceM;

/**
 * Simple META-INF/services registrator
 * Uses <i>net.java.visualvm.services.annotations.ServiceM</i> annotation to identify the service
 * @author Jaroslav Bachorik
 */
@SupportedAnnotationTypes(value = {"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class NBServiceRegistrator extends AbstractProcessor {
    private Collection<ServiceM> globalServices = new ArrayList<ServiceM>();
    
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            try {
                writeRegistrations(globalServices);
            } catch (IOException e) {
                return false;
            }
            return true;
        }
        
        Map<String, ServiceM> services = new HashMap<String, ServiceM>();

        Map<String, ServiceM> persistedServices = new HashMap<String, ServiceM>();
        loadMetaInfServices(persistedServices, StandardLocation.SOURCE_PATH);
        
        loadMetaInfServices(services, StandardLocation.CLASS_OUTPUT);

        for (Element element : roundEnv.getElementsAnnotatedWith(Service.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement serviceElement = (TypeElement) element;
                String serviceName = processingEnv.getElementUtils().getBinaryName(serviceElement).toString();

                ServiceM service = services.get(serviceName);
                if (service == null) {
                    service = new ServiceM(processingEnv.getElementUtils().getBinaryName(serviceElement).toString());
                    services.put(serviceName, service);
                }
                service.getContracts().addAll(findDefinedContracts(serviceElement));
                service.getContracts().addAll(findInheritedContracts(serviceElement));
            }
        }

        invalidateMetaInfServices(services);
        mergeServices(services, persistedServices);
        
        globalServices.addAll(services.values());
        
//        debugRegistrations(services);
        
        return true;
    }

    private void mergeServices(Map<String, ServiceM> toServices, Map<String, ServiceM> fromServices) {
        for(Map.Entry<String, ServiceM> entry : fromServices.entrySet()) {
            ServiceM currentService = toServices.get(entry.getKey());
            if (currentService == null) {
                currentService = new ServiceM(entry.getKey());
                toServices.put(entry.getKey(), currentService);
            }
            currentService.getContracts().addAll(entry.getValue().getContracts());
        }
    }
    
    private void invalidateMetaInfServices(Map<String, ServiceM> services) {
        for(Map.Entry<String, ServiceM> entry : services.entrySet()) {
            TypeElement serviceElement = processingEnv.getElementUtils().getTypeElement(entry.getKey());
            Set<ContractM> actualContracts = new HashSet<ContractM>();
            actualContracts.addAll(findInheritedContracts(serviceElement));
            actualContracts.addAll(findDefinedContracts(serviceElement));
            
            entry.getValue().getContracts().retainAll(actualContracts);
        }        
    }

    private void loadMetaInfServices(Map<String, ServiceM> services, Location serviceLocation) {
        try {
            Field fManagerFld = processingEnv.getFiler().getClass().getDeclaredField("fileManager");
            fManagerFld.setAccessible(true);

            StandardJavaFileManager jfm = (StandardJavaFileManager) fManagerFld.get(processingEnv.getFiler());
            for (File location : jfm.getLocation(serviceLocation)) {
                if (location != null) {
                    processingEnv.getMessager().printMessage(Kind.NOTE, location.getAbsolutePath());
                    loadMetaInfServices(services, location.getAbsolutePath() + File.separator + "META-INF" + File.separator + "services");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMetaInfServices(Map<String, ServiceM> services, String srcPath) throws IOException {
        File servicesFolder = new File(srcPath);
        if (!servicesFolder.exists() || !servicesFolder.isDirectory()) {
            return;
        }
        for (File serviceFile : servicesFolder.listFiles()) {
            if (serviceFile.isFile()) {
                ContractM contract = new ContractM(serviceFile.getName());
                BufferedReader br = new BufferedReader(new FileReader(serviceFile));
                String line;
                try {
                    do {
                        line = br.readLine();
                        if (line != null) {
                            ServiceM service = services.get(line);
                            if (service == null) {
                                service = new ServiceM(line);
                                services.put(line, service);
                            }
                            service.getContracts().add(contract);
                        }
                    } while (line != null);
                } finally {
                    br.close();
                }
            }
        }
    }

    private Set<ContractM> findInheritedContracts(TypeElement serviceElement) {
        if (serviceElement == null) {
            return Collections.EMPTY_SET;
        }
        Set<ContractM> contracts = new HashSet<ContractM>();
        for (TypeMirror contractMirror : serviceElement.getInterfaces()) {
            TypeElement contractElement = (TypeElement) processingEnv.getTypeUtils().asElement(contractMirror);
            if (contractElement.getAnnotation(Contract.class) != null) {
                contracts.add(new ContractM(processingEnv.getElementUtils().getBinaryName(contractElement).toString()));
            }
        }
        contracts.addAll(findInheritedContracts((TypeElement) processingEnv.getTypeUtils().asElement(serviceElement.getSuperclass())));

        return contracts;

    }

    private Set<ContractM> findDefinedContracts(TypeElement serviceElement) {
        if (serviceElement == null) {
            return Collections.EMPTY_SET;
        }
        Set<ContractM> contracts = new HashSet<ContractM>();
        for (AnnotationMirror annMirror : serviceElement.getAnnotationMirrors()) {
            TypeElement annElement = (TypeElement) annMirror.getAnnotationType().asElement();
            String annTypeName = processingEnv.getElementUtils().getBinaryName(annElement).toString();

            if (annTypeName.equals(ContractProvided.class.getName())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annMirror.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().toString().equals("value")) {
                        contracts.add(new ContractM(entry.getValue().toString().replace(".class", "")));
                    }
                }
            }

        }

        return contracts;

    }

    private void debugRegistrations(Map<String, ServiceM> services) {
        for(Map.Entry<String, ServiceM> entry : services.entrySet()) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Service: " + entry.getKey());
            processingEnv.getMessager().printMessage(Kind.NOTE, "==============================================================");
            for(ContractM contract : entry.getValue().getContracts()) {
                processingEnv.getMessager().printMessage(Kind.NOTE, "* " + contract.getContractClass());
            }
        }
    }
    
    private void writeRegistrations(Collection<ServiceM> services) throws IOException {
        Map<String, Set<String>> registrations = new HashMap<String, Set<String>>();
        
        for(ServiceM service : services) {
            for(ContractM contract : service.getContracts()) {
                Set<String> registeredServices = registrations.get(contract.getContractClass());
                if (registeredServices == null) {
                    registeredServices = new HashSet<String>();
                    registrations.put(contract.getContractClass(), registeredServices);
                }
                registeredServices.add(service.getServiceClass());
            }
        }
        
        String servicesPath = null;
        for(Map.Entry<String, Set<String>> entry : registrations.entrySet()) {
            final String METAINF = "META-INF/services";
            String resourcePath = METAINF + "/" + entry.getKey();
            Writer writer = null;
            try {
                FileObject fo = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resourcePath, (Element) null);
                if (servicesPath == null) {
                    servicesPath = fo.toUri().getPath();
                    servicesPath = servicesPath.substring(0, servicesPath.lastIndexOf(METAINF) + METAINF.length());
                }
                writer = fo.openWriter();
                for(String serviceClass : entry.getValue()) {
                    writer.write(serviceClass + "\n");
                }
            } finally {
                if (writer != null) {
                    writer.close();
                } else {
                    throw new IOException();
                }
            }
        }
        
        File servicesDir = new File(servicesPath);
        if (servicesDir != null && servicesDir.isDirectory()) {
            for(File regFile : servicesDir.listFiles()) {
                if (!registrations.containsKey(regFile.getName())) {
                    regFile.delete();
                }
            }
        }
    }
}
