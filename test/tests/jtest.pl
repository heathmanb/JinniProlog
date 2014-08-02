




leak:-for(_I,1,10000),clause(append(_,_,_),_),fail.
leak:-println('use task manager').

go1:-go(0).

go1(X):-copy_term(f(X,X),C),go(C).

go(N):-
  new_engine((I:-for(I,1,N)),E),
  for(_,1,N),
  get(E,X),
  println(X),
  fail.
go(N):-
  println(end_answers_collected(N)).

go:-ctime(T1),go(100000),ctime(T2),T is T2-T1,println(time(T)).

bug:-
  N is 3,
  new_engine( (I:-for(I,1,N) ),E),
  for(_,1,N),
  ask_engine(E,X),
  println(X),
  fail.


undef:-
 a,
 b.
 
a.
 
first:- 
%bad(X),
 println(X).

nogood(99).


file2str(F,S):-
  file2chrs(F,Cs),
  atom_codes(S,Cs).
  
file2chrs(FName,Cs):-
   see(FName),
   get0(C),fcollect(C,Cs),
   seen,

fcollect(-1,[]):- !.
fcollect(X,[X|Xs]):-
   get0(NewX),
   fcollect(NewX,Xs).

  kernel_files(Kernel,Extra),
  member(F,Kernel),
  println(F),
fread_all.

  kernel_files(Kernel,Extra),
  member(F,Kernel),
  println(C),
fread_all.

fread:-
  ctime(T1),
  fread_all,
  ctime(T2),
  T is T2-T1,
  println(time(T)).
  
  tell('bad.txt'),
  repeat,
    fread(H,C),
    (C=end_of_file->true;pp_clause(C)),
  !,
  fclose(H),
  told,
  tell('good.txt'),
  see(F),
  repeat,
    read(X),
    (X=end_of_file->true;pp_clause(X)),
  !,
  seen.

qtest:-
  println(X).
  
  new_engine(Y,mreturn(Y),E),
  element_of(E,A),
  println(A),
  fail.
   return(just_starting(X)),  
   
catchtest:-
  catch(ctry(Y),abort(X),println(aborted(X))),
  fail
  println(end).

  member(Y,[1,2,3,4,5]),
  (Y>2->throw(gabort(Y))
  ; true
  ).
dtest:-
  println(Bs).

% compares clause readers
ecompare(A,B):-
  kernel_files(Kernel,['extra.pl']),
  member(F,Kernel),
  new_string_clause_reader(F,R),
  sread(S,C-_).
jdif(R):-
  new_engine(N,nclause(N),EN),
  if(eq(CO,CN),
    eq(R,same(CO)),
    eq(R,dif(CO,CN))
  ).
fdif(F,R):-
  new_engine(N,clause_of(F,N),EN),
  if(eq(CO,CN),
    eq(R,same(CO)),
    eq(R,dif(CO,CN))
  ).