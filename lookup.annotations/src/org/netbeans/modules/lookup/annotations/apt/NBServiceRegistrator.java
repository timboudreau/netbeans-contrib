/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
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
import java.util.List;
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
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import org.netbeans.modules.lookup.annotations.annotations.Contract;
import org.netbeans.modules.lookup.annotations.annotations.ContractProvided;
import org.netbeans.modules.lookup.annotations.annotations.ContractsProvided;
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
    /**
     * Annotation visitor for traversing all {@linkplain ContractProvided} annotations
     * contained in a {@linkplain ContractsProvided} annotation
     */
    private class ContractsProvidedAnnotationVisitor extends SimpleAnnotationValueVisitor6<Void, Set<ContractM>> {
        @Override
        public Void visitAnnotation(AnnotationMirror a, Set<ContractM> contracts) {
            processContractProvided(a, contracts);
            return null;
        }
        
    }
    
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
        processAnnotatedServices(roundEnv, services);

        invalidateMetaInfServices(services);
        mergeServices(services, persistedServices);
        
        globalServices.addAll(services.values());
        
//        debugRegistrations(services);
        
        return true;
    }

    /**
     * Goes through all classes annotated by {@linkplain Service} annotation
     * and retrieves the contracts implemented
     * 
     * @param roundEnv {@link RoundEnvironment} instance
     * @param services A bag to put the created services
     */
    private void processAnnotatedServices(RoundEnvironment roundEnv, Map<String, ServiceM> services) {

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
    }
    
    /**
     * A utility method to merge two bags of services
     * @param toServices The bag to merge to
     * @param fromServices The to merge from
     */
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
    
    /**
     * This method checks the services from the bag against the actual state
     * All services not comforming to the definition of a service are pruned from the bag
     * @param services The bag of services
     */
    private void invalidateMetaInfServices(Map<String, ServiceM> services) {
        for(Map.Entry<String, ServiceM> entry : services.entrySet()) {
            TypeElement serviceElement = processingEnv.getElementUtils().getTypeElement(entry.getKey());
            Set<ContractM> actualContracts = new HashSet<ContractM>();
            actualContracts.addAll(findInheritedContracts(serviceElement));
            actualContracts.addAll(findDefinedContracts(serviceElement));
            
            entry.getValue().getContracts().retainAll(actualContracts);
        }        
    }

    /**
     * Loads services defined in a <i>META-INF/services</i> location
     * @param services The services bag to load the new services to
     * @param serviceLocation A {@link Location} specifying where to load the services from
     */
    private void loadMetaInfServices(Map<String, ServiceM> services, Location serviceLocation) {
        try {
            // ********** This is a hack to get hold of the source file location; there is no other way :( *******
            Field fManagerFld = processingEnv.getFiler().getClass().getDeclaredField("fileManager"); // NOI18N
            fManagerFld.setAccessible(true);

            StandardJavaFileManager jfm = (StandardJavaFileManager) fManagerFld.get(processingEnv.getFiler());
            // ********* End of hack *****************************************************************************
            for (File location : jfm.getLocation(serviceLocation)) {
                if (location != null) {
                    processingEnv.getMessager().printMessage(Kind.NOTE, location.getAbsolutePath());
                    loadMetaInfServices(services, location.getAbsolutePath() + File.separator + "META-INF" + File.separator + "services"); // NOI18N
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads services defined in a <i>META-INF/services</i> folder under the given path
     * @param services The services bag to load the new services to
     * @param srcPath The path to look for <i>META-INF/services</i>
     * @throws java.io.IOException
     */
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

    /**
     * Finds all {@linkplain Contract}s deined up the class hierarchy starting by the given class
     * @param serviceElement The class to start searching for inherited {@linkplain Contract}s
     * @return Returns a set of inherited contract
     */
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

    /**
     * Retrieves a set of contracts defined by {@linkplain ContractProvided} annotations on a class
     * @param serviceElement The class to inspect
     * @return Returns a set of defined contracts
     */
    private Set<ContractM> findDefinedContracts(TypeElement serviceElement) {
        if (serviceElement == null) {
            return Collections.EMPTY_SET;
        }
        Set<ContractM> contracts = new HashSet<ContractM>();
        for (AnnotationMirror annMirror : serviceElement.getAnnotationMirrors()) {
            TypeElement annElement = (TypeElement) annMirror.getAnnotationType().asElement();
            String annTypeName = processingEnv.getElementUtils().getBinaryName(annElement).toString();

            if (annTypeName.equals(ContractsProvided.class.getName())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annMirror.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().toString().equals("value")) { // NOI18N
                        
                        List<AnnotationValue> contractList = (List<AnnotationValue>)entry.getValue().getValue();
                        for(AnnotationValue contr : contractList) {
                            contr.accept(new ContractsProvidedAnnotationVisitor(), contracts);
                        }
                    }
                }
            }
            processingEnv.getMessager().printMessage(Kind.NOTE, annTypeName);
            if (annTypeName.equals(ContractProvided.class.getName())) {
                processContractProvided(annMirror, contracts);
            }

        }

        return contracts;

    }

    /**
     * Debugging output
     * @param services Bag of services
     */
    private void debugRegistrations(Map<String, ServiceM> services) {
        for(Map.Entry<String, ServiceM> entry : services.entrySet()) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Service: " + entry.getKey()); // NOI18N
            processingEnv.getMessager().printMessage(Kind.NOTE, "=============================================================="); // NOI18N
            for(ContractM contract : entry.getValue().getContracts()) {
                processingEnv.getMessager().printMessage(Kind.NOTE, "* " + contract.getContractClass()); // NOI18N
            }
        }
    }
    
    /**
     * Writes the defined services to <i>META-INF/services</i> location
     * @param services The services to write
     * @throws java.io.IOException
     */
    private void writeRegistrations(Collection<ServiceM> services) throws IOException {
        Map<String, Set<String>> registrations = preprocessRegistrations(services);
        
        String servicesPath = null;
        for(Map.Entry<String, Set<String>> entry : registrations.entrySet()) {
            final String METAINF = "META-INF/services"; // NOI18N
            String resourcePath = METAINF + "/" + entry.getKey();
            Writer writer = null;
            try {
                FileObject fo = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resourcePath, (Element) null); // NOI18N
                if (servicesPath == null) {
                    servicesPath = fo.toUri().getPath();
                    servicesPath = servicesPath.substring(0, servicesPath.lastIndexOf(METAINF) + METAINF.length());
                }
                writer = fo.openWriter();
                for(String serviceClass : entry.getValue()) {
                    writer.write(serviceClass + "\n"); // NOI18N
                }
            } finally {
                if (writer != null) {
                    writer.close();
                } else {
                    throw new IOException();
                }
            }
        }
        cleanupRegistrations(servicesPath, registrations);
    }

    /**
     * A helper method to convert the service registrations from the internal
     * representation to the one compatible with the <i>META-INF/services</i>
     * registration paradigm.
     * @param services The services to process
     * @return Returns an in-memory representation of the <i>META-INF/services</i> registrations
     */
    private Map<String, Set<String>> preprocessRegistrations(Collection<ServiceM> services) {
        Map<String, Set<String>> registrations = new HashMap<String, Set<String>>();
        for (ServiceM service : services) {
            for (ContractM contract : service.getContracts()) {
                Set<String> registeredServices = registrations.get(contract.getContractClass());
                if (registeredServices == null) {
                    registeredServices = new HashSet<String>();
                    registrations.put(contract.getContractClass(), registeredServices);
                }
                registeredServices.add(service.getServiceClass());
            }
        }
        return registrations;
    }
    
    /**
     * A helper method to process the contents of {@linkplain ContractProvided} annotation
     * @param annMirror The annotation mirror
     * @param contracts The set of contracts to add the new contract to
     */
    private void processContractProvided(AnnotationMirror annMirror, Set<ContractM> contracts) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals("value")) {
                contracts.add(new ContractM(entry.getValue().toString().replace(".class", "")));
            }
        }
    }
    
    /**
     * Checks the <i>META-INF/services</i> style registrations in the given path
     * It removes all file-based registrations without their corresponding in-memory
     * representation.
     * @param servicesPath The path to look for the registrations
     * @param registrations The in-memory registrations
     */
    private void cleanupRegistrations(String servicesPath, Map<String, Set<String>> registrations) {
        if (servicesPath != null) {
            File servicesDir = new File(servicesPath);
            if (servicesDir != null && servicesDir.isDirectory()) {
                for (File regFile : servicesDir.listFiles()) {
                    if (!registrations.containsKey(regFile.getName())) {
                        regFile.delete();
                    }
                }
            }
        }
    }
}
