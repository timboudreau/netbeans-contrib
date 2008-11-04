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
package org.netbeans.modules.editor.fscompletion;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Jan Lahoda
 */
@org.openide.util.lookup.ServiceProvider(service=java.net.URLStreamHandlerFactory.class)
public class URLStreamHandlerFactoryImpl implements URLStreamHandlerFactory {
    public static final String PROTOCOL = "fscompl";
    
    public URLStreamHandler createURLStreamHandler(String protocol) {
        if (PROTOCOL.equals(protocol)) {
            return new URLStreamHandlerImpl();
        }
        return null;
    }
    private static class URLStreamHandlerImpl extends URLStreamHandler {
        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return new URLConnectionImpl(u);
        }
    }

    private static class URLConnectionImpl extends URLConnection {
        private final URL unwrappedUrl;

        public URLConnectionImpl(URL wrappedUrl) throws MalformedURLException {
            super(wrappedUrl);
            String u = wrappedUrl.toExternalForm();
            this.unwrappedUrl = new URL(u.substring("fscompl:".length()));
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new DownScalingInputStream(unwrappedUrl);
        }
    }

    private static final int MAX_WIDTH = 400;
    private static final int MAX_HEIGHT = 250;
    
    private static final class DownScalingInputStream extends InputStream {
        private final InputStream delegate;

        public DownScalingInputStream(URL url) throws IOException {
            BufferedImage bi = ImageIO.read(url);

            new ImageIcon(bi);

            if (bi.getWidth() > MAX_WIDTH || bi.getHeight() > MAX_HEIGHT) {
                double widthRS = ((double) MAX_WIDTH) / bi.getWidth();
                double heightRS = ((double) MAX_HEIGHT) / bi.getHeight();
                double resize = Math.min(widthRS, heightRS);

                bi = resize(bi, (int) (resize * bi.getWidth()), (int) (resize * bi.getHeight()));
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bi, "png", baos);

            delegate = new ByteArrayInputStream(baos.toByteArray());
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }
    }

    private static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }
}
