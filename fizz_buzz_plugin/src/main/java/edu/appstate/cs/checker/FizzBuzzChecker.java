package edu.appstate.cs.checker;

import com.google.auto.service.AutoService;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.matchers.Description;
import com.sun.source.tree.*;

import javax.lang.model.element.Name;

import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.WARNING;

@AutoService(BugChecker.class)
@BugPattern(name = "FizzBuzzChecker", summary = "code quality checks", severity = WARNING, linkType = CUSTOM, link = "https://github.com/calvinpettis/cals_analyzer")

public class FizzBuzzChecker extends BugChecker implements
    BugChecker.VariableTreeMatcher,
    BugChecker.AssignmentTreeMatcher,
    BugChecker.ClassTreeMatcher,
    BugChecker.MethodTreeMatcher,
    BugChecker.ImportTreeMatcher,
    BugChecker.WhileLoopTreeMatcher,
    BugChecker.TryTreeMatcher,
    BugChecker.IfTreeMatcher {

  @Override
  public Description matchVariable(VariableTree tree, VisitorState state) {
    // length check
    String response;
    if (tree.getName().length() > 25) {
      response = "Variable name is too long: %s";
    } else if (tree.getName().length() < 3) {
      response = "Make your variable name more descriptive: %s";
    } else if (tree.getType() == null) {
      response = "Possible null issues here: %s";
    } else if (tree.getName().charAt(0) < 97 || tree.getName().charAt(0) > 122) {
      response = "Naming convention does not follow camelCase: %s";
    } else {
      return Description.NO_MATCH;
    }
    return buildDescription(tree)
        .setMessage(String.format(response, tree.getName()))
        .build();
  }

  @Override
  public Description matchAssignment(AssignmentTree tree, VisitorState state) {
    return Description.NO_MATCH;
  }

  @Override
  public Description matchTry(TryTree tree, VisitorState state) {
    if (tree == null) {
      return buildDescription(tree)
          .setMessage(String.format("You do not check for any exceptions."))
          .build();
    }
    return Description.NO_MATCH;
  }

  @Override
  public Description matchClass(ClassTree tree, VisitorState state) {
    if (tree.getSimpleName().length() > 25) {
      return buildDescription(tree)
          .setMessage(String.format("Class name is too long: %s", tree.getSimpleName()))
          .build();
    }
    return Description.NO_MATCH;
  }

  @Override
  public Description matchMethod(MethodTree tree, VisitorState state) {
    if (tree.getReturnType() == null) {
      return buildDescription(tree)
          .setMessage(String.format("Possible null return error here: %s", tree.getName()))
          .build();
    }
    return Description.NO_MATCH;
  }

  @Override
  public Description matchImport(ImportTree tree, VisitorState state) {
    if (tree.isStatic()) {
      return buildDescription(tree)
          .setMessage(String.format("Did you mean to statically import?"))
          .build();
    }
    return Description.NO_MATCH;
  }

  @Override
  public Description matchIf(IfTree tree, VisitorState state) {
    if (tree.getCondition().equals("false") && tree.getElseStatement() != null) {
      return buildDescription(tree)
          .setMessage(String.format("dead code: %s", tree.getClass()))
          .build();
    }
    return Description.NO_MATCH;
  }

  @Override
  public Description matchWhileLoop(WhileLoopTree tree, VisitorState state) {
    if (tree.getStatement().toString() == "true") {
      return buildDescription(tree)
          .setMessage(String.format("did you mean to have an infinite loop?: %s", tree.getClass()))
          .build();
    }
    return Description.NO_MATCH;
  }
}
