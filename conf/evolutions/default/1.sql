# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table activity (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  problem_id                bigint,
  discussion_id             bigint,
  level                     integer,
  activity_type             integer,
  meta_data                 varchar(255),
  create_time               datetime,
  constraint pk_activity primary key (id))
;

create table client (
  id                        integer auto_increment not null,
  name                      varchar(255),
  secret                    varchar(255),
  constraint pk_client primary key (id))
;

create table contest (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  description               varchar(255),
  begin_time                datetime,
  duration                  integer,
  is_public                 tinyint(1) default 0,
  password                  varchar(255),
  manager_id                bigint,
  status                    integer,
  create_time               datetime,
  constraint pk_contest primary key (id))
;

create table contest_participant (
  id                        bigint auto_increment not null,
  contest_id                bigint,
  user_id                   bigint,
  create_time               datetime,
  constraint pk_contest_participant primary key (id))
;

create table contest_problem (
  id                        bigint auto_increment not null,
  contest_id                bigint,
  problem_id                bigint,
  slug                      varchar(255),
  constraint pk_contest_problem primary key (id))
;

create table discussion (
  id                        bigint auto_increment not null,
  title                     varchar(255),
  problem_id                bigint,
  solution_id               bigint,
  contest_id                bigint,
  parent_id                 bigint,
  user_id                   bigint,
  content                   longtext,
  create_time               datetime,
  last_reply_time           datetime,
  contain_solution          tinyint(1) default 0,
  always_on_top             integer,
  constraint pk_discussion primary key (id))
;

create table discussion_follower (
  id                        bigint auto_increment not null,
  discussion_id             bigint,
  user_id                   bigint,
  create_time               datetime,
  constraint pk_discussion_follower primary key (id))
;

create table judge (
  id                        integer auto_increment not null,
  nickname                  varchar(255),
  secret                    varchar(255),
  sort                      integer,
  constraint pk_judge primary key (id))
;

create table mail (
  id                        bigint auto_increment not null,
  subject                   varchar(255),
  receiver                  varchar(255),
  content                   longtext,
  status                    integer,
  create_time               datetime,
  user_id                   bigint,
  constraint pk_mail primary key (id))
;

create table problem (
  id                        bigint auto_increment not null,
  slug                      varchar(255) not null,
  status                    integer,
  title                     varchar(255),
  description               longtext,
  tags                      varchar(255),
  source                    varchar(255),
  time_limit                integer,
  memory_limit              integer,
  special_judge             tinyint(1) default 0,
  resources_hash            varchar(255),
  create_time               datetime,
  last_modify_time          datetime,
  author_id                 bigint,
  show_in_problems          tinyint(1) default 0,
  constraint uq_problem_slug unique (slug),
  constraint pk_problem primary key (id))
;

create table problem_follower (
  id                        bigint auto_increment not null,
  problem_id                bigint,
  user_id                   bigint,
  create_time               datetime,
  constraint pk_problem_follower primary key (id))
;

create table problem_star (
  id                        bigint auto_increment not null,
  problem_id                bigint,
  user_id                   bigint,
  create_time               datetime,
  constraint pk_problem_star primary key (id))
;

create table problem_vote (
  id                        bigint auto_increment not null,
  problem_id                bigint,
  user_id                   bigint,
  rating                    integer,
  difficulty                integer,
  create_time               datetime,
  constraint pk_problem_vote primary key (id))
;

create table solution (
  id                        bigint auto_increment not null,
  user_id                   bigint,
  is_private                tinyint(1) default 0,
  contest_id                bigint,
  language                  integer,
  code                      longtext,
  problem_id                bigint,
  submit_time               datetime,
  dispatch_time             datetime,
  judge_time                datetime,
  judge_id                  integer,
  result                    integer,
  judge_response            longtext,
  time_used                 integer,
  memory_used               integer,
  constraint pk_solution primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  email                     varchar(255) not null,
  is_email_verified         tinyint(1) default 0,
  last_email_modified_time  datetime,
  last_verification_email_sent_time datetime,
  reset_password_requested_time datetime,
  password                  varchar(255),
  secret                    varchar(255),
  admin_level               integer,
  status                    integer,
  gender                    tinyint(1) default 0,
  display_name              varchar(255),
  school                    varchar(255),
  country                   varchar(255),
  link                      varchar(255),
  description               longtext,
  create_time               datetime,
  last_login_time           datetime,
  constraint uq_user_name unique (name),
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

create table user_relation (
  id                        bigint auto_increment not null,
  follower_id               bigint,
  following_id              bigint,
  create_time               datetime,
  constraint pk_user_relation primary key (id))
;


create table discussion_user (
  discussion_id                  bigint not null,
  user_id                        bigint not null,
  constraint pk_discussion_user primary key (discussion_id, user_id))
;
alter table activity add constraint fk_activity_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_activity_user_1 on activity (user_id);
alter table activity add constraint fk_activity_problem_2 foreign key (problem_id) references problem (id) on delete restrict on update restrict;
create index ix_activity_problem_2 on activity (problem_id);
alter table activity add constraint fk_activity_discussion_3 foreign key (discussion_id) references discussion (id) on delete restrict on update restrict;
create index ix_activity_discussion_3 on activity (discussion_id);
alter table contest add constraint fk_contest_manager_4 foreign key (manager_id) references user (id) on delete restrict on update restrict;
create index ix_contest_manager_4 on contest (manager_id);
alter table contest_participant add constraint fk_contest_participant_contest_5 foreign key (contest_id) references contest (id) on delete restrict on update restrict;
create index ix_contest_participant_contest_5 on contest_participant (contest_id);
alter table contest_participant add constraint fk_contest_participant_user_6 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_contest_participant_user_6 on contest_participant (user_id);
alter table contest_problem add constraint fk_contest_problem_contest_7 foreign key (contest_id) references contest (id) on delete restrict on update restrict;
create index ix_contest_problem_contest_7 on contest_problem (contest_id);
alter table contest_problem add constraint fk_contest_problem_problem_8 foreign key (problem_id) references problem (id) on delete restrict on update restrict;
create index ix_contest_problem_problem_8 on contest_problem (problem_id);
alter table discussion add constraint fk_discussion_problem_9 foreign key (problem_id) references problem (id) on delete restrict on update restrict;
create index ix_discussion_problem_9 on discussion (problem_id);
alter table discussion add constraint fk_discussion_solution_10 foreign key (solution_id) references solution (id) on delete restrict on update restrict;
create index ix_discussion_solution_10 on discussion (solution_id);
alter table discussion add constraint fk_discussion_contest_11 foreign key (contest_id) references contest (id) on delete restrict on update restrict;
create index ix_discussion_contest_11 on discussion (contest_id);
alter table discussion add constraint fk_discussion_parent_12 foreign key (parent_id) references discussion (id) on delete restrict on update restrict;
create index ix_discussion_parent_12 on discussion (parent_id);
alter table discussion add constraint fk_discussion_user_13 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_discussion_user_13 on discussion (user_id);
alter table discussion_follower add constraint fk_discussion_follower_discussion_14 foreign key (discussion_id) references discussion (id) on delete restrict on update restrict;
create index ix_discussion_follower_discussion_14 on discussion_follower (discussion_id);
alter table discussion_follower add constraint fk_discussion_follower_user_15 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_discussion_follower_user_15 on discussion_follower (user_id);
alter table mail add constraint fk_mail_user_16 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_mail_user_16 on mail (user_id);
alter table problem add constraint fk_problem_author_17 foreign key (author_id) references user (id) on delete restrict on update restrict;
create index ix_problem_author_17 on problem (author_id);
alter table problem_follower add constraint fk_problem_follower_problem_18 foreign key (problem_id) references problem (id) on delete restrict on update restrict;
create index ix_problem_follower_problem_18 on problem_follower (problem_id);
alter table problem_follower add constraint fk_problem_follower_user_19 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_problem_follower_user_19 on problem_follower (user_id);
alter table problem_star add constraint fk_problem_star_problem_20 foreign key (problem_id) references problem (id) on delete restrict on update restrict;
create index ix_problem_star_problem_20 on problem_star (problem_id);
alter table problem_star add constraint fk_problem_star_user_21 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_problem_star_user_21 on problem_star (user_id);
alter table problem_vote add constraint fk_problem_vote_problem_22 foreign key (problem_id) references problem (id) on delete restrict on update restrict;
create index ix_problem_vote_problem_22 on problem_vote (problem_id);
alter table problem_vote add constraint fk_problem_vote_user_23 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_problem_vote_user_23 on problem_vote (user_id);
alter table solution add constraint fk_solution_user_24 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_solution_user_24 on solution (user_id);
alter table solution add constraint fk_solution_contest_25 foreign key (contest_id) references contest (id) on delete restrict on update restrict;
create index ix_solution_contest_25 on solution (contest_id);
alter table solution add constraint fk_solution_problem_26 foreign key (problem_id) references problem (id) on delete restrict on update restrict;
create index ix_solution_problem_26 on solution (problem_id);
alter table solution add constraint fk_solution_judge_27 foreign key (judge_id) references judge (id) on delete restrict on update restrict;
create index ix_solution_judge_27 on solution (judge_id);
alter table user_relation add constraint fk_user_relation_follower_28 foreign key (follower_id) references user (id) on delete restrict on update restrict;
create index ix_user_relation_follower_28 on user_relation (follower_id);
alter table user_relation add constraint fk_user_relation_following_29 foreign key (following_id) references user (id) on delete restrict on update restrict;
create index ix_user_relation_following_29 on user_relation (following_id);



alter table discussion_user add constraint fk_discussion_user_discussion_01 foreign key (discussion_id) references discussion (id) on delete restrict on update restrict;

alter table discussion_user add constraint fk_discussion_user_user_02 foreign key (user_id) references user (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table activity;

drop table client;

drop table contest;

drop table contest_participant;

drop table contest_problem;

drop table discussion;

drop table discussion_user;

drop table discussion_follower;

drop table judge;

drop table mail;

drop table problem;

drop table problem_follower;

drop table problem_star;

drop table problem_vote;

drop table solution;

drop table user;

drop table user_relation;

SET FOREIGN_KEY_CHECKS=1;

