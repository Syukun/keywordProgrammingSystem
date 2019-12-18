package editor.handler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

public class Handler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		String path = "/Users/aochishu/git/keywordProgrammingSystem/keywordProgrammingSystem/SourceCode/previousSourceCodes";
		initialSourceFile(path);
		
		final IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		int cursorPos = getCursorPosition(activeEditor);
		IDocument doc = getCurrentDocument(activeEditor);
		int lineNum = getLineNumber(doc, cursorPos);
		String context = doc.get();
		String processedFile = rewrite(context);
		String previousContext = getPreviousContext(processedFile, lineNum);
		
		MessageDialog.openInformation(window.getShell(), "Setting is finished",
//				previousContext
				"File has already been setting in \n/Users/aochishu/git/keywordProgrammingSystem/keywordProgrammingSystem/SourceCode/previousSourceCodes"
				);
		return null;
	}

	private void initialSourceFile(String path) {
		final IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor();
		int cursorPos = getCursorPosition(activeEditor);
		IDocument doc = getCurrentDocument(activeEditor);
		int lineNum = getLineNumber(doc, cursorPos);
		String context = doc.get();
		String processedFile = rewrite(context);
		String previousContext = getPreviousContext(processedFile, lineNum);
		writePreviousContext(previousContext, path);
	}

	private IDocument getCurrentDocument(final IEditorPart activeEditor) {
		return ((AbstractDecoratedTextEditor) activeEditor).getDocumentProvider()
				.getDocument(activeEditor.getEditorInput());
	}

	private void writePreviousContext(String previousContext, String path) {
		File w = new File(path);
		try {
			FileWriter fileWriter = new FileWriter(w, false);
			fileWriter.write(previousContext);
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Get previous context according to cursor position
	 * 
	 * @param processedFile
	 * @param line
	 * @return
	 */
	private String getPreviousContext(String processedFile, int line) {
		StringBuffer res = new StringBuffer();
		String[] seq = processedFile.split("[\n|\r]");
		try {
			for (int i = 0; i < line; i++) {
				res.append(seq[i] + "\n ");
			}

		} catch (Exception e) {
			System.out.println("Something wrong");
		}
		return res.toString();
	}

	/**
	 * <p>
	 * <del>Extract SingleVariableDeclaration Node to
	 * "Singlevariabledeclaration"<br>
	 * </del> <del>Extract VariableDeclarationFragment Node to
	 * "Variabledeclarationfragment"<br>
	 * <del> Extract StringLiteral Node to "Stringliteral"<br>
	 * Extract NumberLiteral Node to "Numberliteral"<br>
	 * 
	 * </p>
	 * 
	 * @param context
	 * @return String
	 */
	private String rewrite(String context) {
		String res = "";
		IDocument document = new Document(context);
		// Parse
		ASTParser parser = ASTParser.newParser(AST.JLS12);
		parser.setSource(context.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		// Rewrite
		AST ast = cu.getAST();
		ASTRewrite rewrite = ASTRewrite.create(ast);

		cu.accept(new ASTVisitor() {
			public boolean visit(StringLiteral node) {
				SimpleName newName = ast.newSimpleName("Stringliteral");
				rewrite.replace(node, newName, null);
				return false;
			}

			public boolean visit(NumberLiteral node) {
				SimpleName newName = ast.newSimpleName("Numberliteral");
				rewrite.replace(node, newName, null);
				return false;
			}
		});

		TextEdit edits = rewrite.rewriteAST(document, null);
		UndoEdit undo = null;
		try {
			undo = edits.apply(document);
		} catch (MalformedTreeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		res = document.get();

		return res;
	}

	private int getLineNumber(IDocument doc, int cursorPos) {
		int line = -1;
		try {
			line = doc.getLineOfOffset(cursorPos);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;
	}

	/**
	 * get cursor position
	 * 
	 * @param activeEditor
	 * @return
	 */
	private int getCursorPosition(IEditorPart activeEditor) {
		int cursorPos = -1;
		if (activeEditor instanceof ITextEditor) {
			ISelectionProvider selectionProvider = ((ITextEditor) activeEditor).getSelectionProvider();
			ISelection selection = selectionProvider.getSelection();
			if (selection instanceof ITextSelection) {
				ITextSelection textSelection = (ITextSelection) selection;
				cursorPos = textSelection.getOffset();
			}
		}
		return cursorPos;
	}


}
