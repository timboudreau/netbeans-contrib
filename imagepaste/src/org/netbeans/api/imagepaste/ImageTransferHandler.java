/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */
package org.netbeans.api.imagepaste;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.modules.imagepaste.imgedit.CropPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;


class ImageTransferHandler extends TransferHandler {
    protected final JEditorPane ed;
    protected final PasteInfoProvider provider;

    public ImageTransferHandler(JEditorPane ed, PasteInfoProvider provider) {
        super ("text"); //NOI18N
        this.ed = ed;
        this.provider = provider;
        assert ed != null;
    }

    public boolean importData(JComponent comp, Transferable t) {
        boolean supported = t.isDataFlavorSupported(DataFlavor.imageFlavor);
        if (supported) {
            return importImage (t);
        } else {
            boolean result = super.importData (comp, t);
            return result;
        }
    }

    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        if (Arrays.asList (transferFlavors).indexOf(DataFlavor.imageFlavor)  != -1) {
            return true;
        }  else {
            boolean result = super.canImport(comp, transferFlavors);
            return result;
        }
    }

    public int getSourceActions(JComponent c) {
        int result;
        result = super.getSourceActions(c);
        return result;
    }

    public Icon getVisualRepresentation(Transferable t) {
        Icon result;
        result = super.getVisualRepresentation(t);
        return result;
    }

    protected Transferable createTransferable(JComponent c) {
        Transferable result;
        result = super.createTransferable(c);
        return result;
    }

    protected void exportDone(JComponent source, Transferable data, int action) {
        super.exportDone(source, data, action);
    }

    
    private boolean importImage(Transferable t) {
        boolean result = doImportImage(t);
        return result;
    }
    
    private boolean doImportImage(Transferable t) {
        ProgressHandle prg = ProgressHandleFactory.createHandle(
                NbBundle.getMessage(ImageTransferHandler.class, 
                "Importing_Image")); //NOI18N
        try {
            prg.start();
            TopComponent tc = (TopComponent) SwingUtilities.getAncestorOfClass(
                    TopComponent.class, ed);
            if (tc == null) {
                return false;
            }
            DataObject ob = (DataObject) tc.getLookup().lookup (DataObject.class);
            if (ob == null) {
                return false;
            }
            FileObject fob = ob.getPrimaryFile();
            File file = FileUtil.toFile(fob);
            if (file == null) {
                return false;
            }
            
            prg.setDisplayName(NbBundle.getMessage(ImageTransferHandler.class, 
                    "Fetching_image_from_clipboard")); //NOI18N
            Image image = (Image)t.getTransferData(DataFlavor.imageFlavor);
            BufferedImage im;
            if (image instanceof BufferedImage) {
                im = (BufferedImage) image;
            }  else {
                prg.setDisplayName(NbBundle.getMessage(
                        ImageTransferHandler.class, "Processing_image")); //NOI18N
                MediaTracker track = new MediaTracker (ed);
                track.waitForAll(10000);
                if (!track.checkAll()) {
                    StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(
                        ImageTransferHandler.class, 
                        "Failed_to_load_image_from_clipboard")); //NOI18N
                }

                BufferedImage img =
                        ed.getGraphicsConfiguration().createCompatibleImage(
                        image.getWidth(ed),
                        image.getHeight(ed));

                Graphics2D g = img.createGraphics();
                g.drawImage(image, AffineTransform.getTranslateInstance(0,0),
                        ed);
                im = img;
            }

            prg.setDisplayName (NbBundle.getMessage(ImageTransferHandler.class,
                    "Cropping_image")); //NOI18N

            final CropPanel cropper = new CropPanel();
            cropper.setImage (im);
            final DialogDescriptor cdlg = new DialogDescriptor (cropper, 
                    NbBundle.getMessage(ImageTransferHandler.class, 
                    "Crop_Image")); //NOI18N
            ActionListener al = new ActionListener() {
                boolean firstTime = true;
                public void actionPerformed (ActionEvent ae) {
                    if (firstTime) {
                        //XXX receives Enter keystroke that closed the
                        //previous dialog...
                        firstTime = false;
                        return;
                    }
                }
            };
            cropper.addActionListener (al);
            if (!DialogDescriptor.OK_OPTION.equals(DialogDisplayer.getDefault().notify(cdlg))) {
                return false;
            }
            im = cropper.getImage();

            PasteInfo info = provider.getPasteInfo(file);
            if (info == null) {
                return false;
            }
            
            File dest = info.destFile;
            prg.setDisplayName(NbBundle.getMessage(ImageTransferHandler.class, 
                    "Finding_target_folder")); //NOI18N
            if (!dest.exists()) {
                File parent = dest.getParentFile();
                if (!parent.exists() && !parent.mkdirs()) {
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(ImageTransferHandler.class,
                            "MSG_CANNOTCREATE", parent)); //NOI18N
                    return false;
                }
                
                prg.setDisplayName(NbBundle.getMessage(
                        ImageTransferHandler.class, "Creating_output_file", //NOI18N
                        dest.getName())); //NOI18N
                if (!dest.createNewFile()) {
                    StatusDisplayer.getDefault().setStatusText(
                            NbBundle.getMessage(ImageTransferHandler.class,
                            "MSG_CANNOTCREATE", dest)); //NOI18N
                    return false;
                }
            } else {
                NotifyDescriptor dd = new DialogDescriptor(
                        NbBundle.getMessage(
                        ImageTransferHandler.class, "MSG_OVERWRITE", dest), //NOI18N
                        NbBundle.getMessage(
                        ImageTransferHandler.class, "TTL_FILE_EXISTS", dest)); //NOI18N
                if (DialogDisplayer.getDefault().notify(dd) != NotifyDescriptor.OK_OPTION) {
                    return false;
                }
            }
            try {
                prg.setDisplayName(NbBundle.getMessage(
                        ImageTransferHandler.class, "Writing_image_data")); //NOI18N
                ImageIO.write(im, info.imgFormat, dest);
            }  catch (IOException e) {
                Exceptions.printStackTrace(e);
                return false;
            }
            prg.setDisplayName(NbBundle.getMessage(ImageTransferHandler.class, 
                    "INSERTING_REFERENCE")); //NOI18N
            String txt = info.pasteText;
            ed.replaceSelection(txt);
            return true;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }  finally {
            prg.finish();
        }
    }
}