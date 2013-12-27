package remotedeploy.dialog;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Folder;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class SelectFileUtil {

	public static java.io.File getSelectFile() {
		IWorkbench wb = PlatformUI.getWorkbench();
		IWorkbenchWindow  ww  = wb.getActiveWorkbenchWindow();
		ISelectionService ss = ww.getSelectionService();
		
		IStructuredSelection structured = (IStructuredSelection)ss.getSelection("org.eclipse.jdt.ui.PackageExplorer");

		if(structured == null){
			structured = (IStructuredSelection)ss.getSelection("org.eclipse.jdt.ui.ProjectExplorer");
			if(structured == null){
				return new java.io.File("");
			}
		}
		
		Object selected = structured.getFirstElement();

		if (selected instanceof File) {

			File file = (File) selected;

			return file.getLocation().toFile();

		} else if (selected instanceof Folder) {

			Folder folder = (Folder) selected;

			return  folder.getLocation().toFile();

		}
		return null;

	}
}
