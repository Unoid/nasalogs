desc logs;

select count(*) from logs;
select * from logs;

select dayofmonth(date), endpoint, count(*) as count
from logs
where page = true and dayofmonth(date) = 1
group by endpoint
order by count desc
limit 10;

-- 1. Top 10 popular pages between 0000 EST and 0800 EST on a daily basis.
select date(date) as date, endpoint, count(*) as count
from logs
where page = true and date BETWEEN '1995-07-01 00:00:00' AND '1995-07-01 08:00:00'
group by endpoint
order by count desc
limit 10;

-- 2. Top 10 popular pages between 0800 EST and 1000 EST on a daily basis.
select date(date) as date, endpoint, count(*) as count
from logs
where page = true and date BETWEEN '1995-07-01 08:00:00' AND '1995-07-01 10:00:00'
group by endpoint
order by count desc
limit 10;

-- 3. Top 10 popular pages for July.
select endpoint, count(*) as count
from logs
where page = true
group by endpoint
order by count desc
limit 10;

-- 1. List of source getting 400 responses, sorted in descending order of number of 400 responses.
select host, count(response) as count
from logs
where response >= 400 AND response < 500
group by host
order by count desc;

-- 2. A list of unique URLs that got 400 response from the servers.
select distinct(endpoint)
from logs
where response >= 400 AND response < 500
order by endpoint;

-- 3. The number of 200, 400 and 500 responses per URL per day.
select date(date) as day, endpoint, response, count(*) as count
from logs
group by dayofmonth(day), endpoint, response;